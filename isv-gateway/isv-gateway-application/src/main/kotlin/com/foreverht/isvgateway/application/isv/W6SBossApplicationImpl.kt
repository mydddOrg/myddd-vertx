package com.foreverht.isvgateway.application.isv

import com.foreverht.isvgateway.application.W6SBossApplication
import com.foreverht.isvgateway.application.workplus.resultSuccess
import com.foreverht.isvgateway.domain.*
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusISV
import io.vertx.core.Future
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class W6SBossApplicationImpl:W6SBossApplication {

    private val webClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    companion object {
        private const val PERMANENT_URL = "%s/permanent-code?access_token=%s"
    }

    override suspend fun requestISVToken(clientId: String): Future<ISVClientToken?> {
        return try {
            val existsIsvClientToken = ISVClientToken.queryByClientId(clientId = clientId).await()
            if (Objects.nonNull(existsIsvClientToken)){
                Future.succeededFuture(existsIsvClientToken)
            }
            else{
                val isvClientToken = generateToken(clientId).await()
                Future.succeededFuture(isvClientToken)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun requestPermanentCode(clientId: String, orgId: String): Future<ISVAuthCode> {
        return try {
            val isvClient = ISVClient.queryClient(clientId = clientId).await()
            if (Objects.isNull(isvClient)) {
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }

            val isvClientToken = requestISVToken(clientId = clientId).await()
            if(Objects.isNull(isvClientToken)){
                throw BusinessLogicException(ISVErrorCode.REMOTE_CLIENT_TOKEN_REQUEST_FAIL)
            }

            val extra = isvClient!!.extra as ISVClientExtraForWorkPlusISV

            val tmpAuthCode = ISVAuthCode.queryTemporaryAuthCode(suiteId = extra.suiteKey,orgId = orgId,clientType = ISVClientType.WorkPlusISV).await()
            if(Objects.isNull(tmpAuthCode)){
                throw BusinessLogicException(ISVErrorCode.TEMPORARY_CODE_NOT_FOUND)
            }

            val requestBody = json {
                obj(
                    "suite_key" to extra.suiteKey,
                    "tmp_auth_code" to tmpAuthCode!!.temporaryAuthCode
                )
            }

            val requestUrl = String.format(PERMANENT_URL,extra.isvApi,isvClientToken!!.token)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(requestBody)
                .await()
            if(response.resultSuccess()){
                val body = response.bodyAsJsonObject()
                tmpAuthCode!!.permanentAuthCode = body.getJsonObject("result").getString("permanent_code")
                val permanentCode = tmpAuthCode.toPermanent().await()
                Future.succeededFuture(permanentCode)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    internal suspend fun generateToken(clientId: String): Future<ISVClientToken?> {
        return try {
            val isvClient = ISVClient.queryClient(clientId = clientId).await()
            if (Objects.isNull(isvClient)) {
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }
            val (accessToken, expireTime) = requestSuiteToken(isvClient = isvClient!!).await()

            val tokenExtra =ISVClientTokenExtraForWorkPlusISV.createInstance(accessToken = accessToken, expireTime = expireTime)
            val isvClientToken = ISVClientToken.createInstanceByExtra(clientId = clientId, extra = tokenExtra)

            val createdISVClientToken = isvClientToken.saveClientToken().await()
            return Future.succeededFuture(createdISVClientToken)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    internal suspend fun requestSuiteToken(isvClient: ISVClient):Future<Pair<String,Long>>{
        return try {
            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV
            val suiteKey = extra.suiteKey
            val suiteTicket = ISVSuiteTicket.querySuiteTicket(suiteId = suiteKey,clientType = ISVClientType.WorkPlusISV).await()
            if(Objects.isNull(suiteTicket)){
                throw BusinessLogicException(ISVErrorCode.SUITE_KEY_MISSING)
            }

            val requestJson = json {
                obj(
                    "vendor_key" to extra.vendorKey,
                    "suite_key" to suiteKey,
                    "suite_secret" to extra.suiteSecret,
                    "suite_ticket" to suiteTicket!!.suiteTicket
                )
            }

            val requestApi = "${extra.isvApi}/access-token"

            val response = webClient.postAbs(requestApi)
                .sendJsonObject(requestJson)
                .await()
            if(response.resultSuccess()){
                val result = response.bodyAsJsonObject().getJsonObject("result")
                Future.succeededFuture(Pair(result.getString("access_token"),result.getLong("expire_time")))
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}