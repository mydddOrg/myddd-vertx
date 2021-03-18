package com.foreverht.isvgateway.application.isv

import com.foreverht.isvgateway.application.W6SBossApplication
import com.foreverht.isvgateway.application.workplus.resultSuccess
import com.foreverht.isvgateway.domain.*
import com.foreverht.isvgateway.domain.extra.ISVAuthExtraForISV
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
        private const val ACTIVATE_SUITE_URL = "%s/suite-activation?access_token=%s"
        private const val API_ACCESS_TOKEN = "%s/access-api-token?access_token=%s"

        private const val SUITE_KEY = "suite_key"
        private const val PERMANENT_CODE = "permanent_code"
        private const val VENDOR_KEY = "vendor_key"
        private const val SUITE_TICKET = "suite_ticket"
        private const val SUITE_SECRET = "suite_secret"
        private const val TEMPORARY_AUTH_CODE = "tmp_auth_code"
        private const val DOMAIN_ID = "domain_id"
        private const val ORG_ID = "org_id"
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
            val (isvClient,isvClientToken) = requestClientBaseInfo(clientId = clientId,orgId = orgId).await()

            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV

            val authCode = ISVAuthCode.queryAuthCode(suiteId = extra.suiteKey,orgId = orgId,clientType = ISVClientType.WorkPlusISV).await()
            if(authCode?.authStatus == ISVAuthStatus.Permanent){
                 Future.succeededFuture(authCode)
            }else{
                val tmpAuthCode = ISVAuthCode.queryTemporaryAuthCode(suiteId = extra.suiteKey,orgId = orgId,clientType = ISVClientType.WorkPlusISV).await()
                if(Objects.isNull(tmpAuthCode)){
                    throw BusinessLogicException(ISVErrorCode.TEMPORARY_CODE_NOT_FOUND)
                }

                val requestBody = json {
                    obj(
                        SUITE_KEY to extra.suiteKey,
                        TEMPORARY_AUTH_CODE to tmpAuthCode!!.temporaryAuthCode
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
            }


        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun activeSuite(clientId: String, orgId: String): Future<Boolean> {
        return try {
            val (isvClient,isvClientToken,permanentAuthCode) = requestClientInfo(clientId = clientId,orgId = orgId).await()

            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV

            val requestUrl = String.format(ACTIVATE_SUITE_URL,extra.isvApi, isvClientToken.token)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(
                    json {
                        obj(
                            SUITE_KEY to extra.suiteKey,
                            PERMANENT_CODE to permanentAuthCode!!.permanentAuthCode
                        )
                    }
                ).await()

            if(response.resultSuccess()){
                Future.succeededFuture(true)
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun requestApiAccessToken(clientId: String, orgId: String): Future<ISVAuthCode> {
        return try {
            val (isvClient,isvClientToken,permanentAuthCode) = requestClientInfo(clientId = clientId,orgId = orgId).await()
            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV

            val requestJson = json {
                obj(
                    SUITE_KEY to extra.suiteKey,
                    PERMANENT_CODE to permanentAuthCode.permanentAuthCode,
                    DOMAIN_ID to permanentAuthCode.domainId,
                    ORG_ID to permanentAuthCode.orgId,
                )
            }
            val requestUrl = String.format(API_ACCESS_TOKEN,extra.isvApi,isvClientToken.token)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(requestJson)
                .await()

            if(response.resultSuccess()){
                val body = response.bodyAsJsonObject()
                val isvAuthExtra = ISVAuthExtraForISV.createInstanceFromJson(body.getJsonObject("result"))
                val saved = permanentAuthCode.saveApiExtra(isvAuthExtra).await()
                Future.succeededFuture(saved)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun requestClientBaseInfo(clientId: String,orgId: String):Future<Pair<ISVClient,ISVClientToken>>{
        return try {
            val isvClient = ISVClient.queryClient(clientId = clientId).await()
            if (Objects.isNull(isvClient)) {
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }

            val isvClientToken = requestISVToken(clientId = clientId).await()
            if(Objects.isNull(isvClientToken)){
                throw BusinessLogicException(ISVErrorCode.REMOTE_CLIENT_TOKEN_REQUEST_FAIL)
            }

            Future.succeededFuture(Pair(isvClient!!, isvClientToken!!))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    private suspend fun requestClientInfo(clientId: String,orgId: String):Future<Triple<ISVClient,ISVClientToken,ISVAuthCode>>{
        return try {

            val (isvClient,isvClientToken) = requestClientBaseInfo(clientId = clientId,orgId = orgId).await()

            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV

            val permanentAuthCode = ISVAuthCode.queryPermanentAuthCode(suiteId = extra.suiteKey,orgId = orgId,clientType = ISVClientType.WorkPlusISV).await()
            if(Objects.isNull(permanentAuthCode)){
                throw BusinessLogicException(ISVErrorCode.PERMANENT_CODE_NOT_FOUND)
            }

            Future.succeededFuture(Triple(isvClient,isvClientToken!!,permanentAuthCode!!))
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
                    VENDOR_KEY to extra.vendorKey,
                    SUITE_KEY to suiteKey,
                    SUITE_SECRET to extra.suiteSecret,
                    SUITE_TICKET to suiteTicket!!.suiteTicket
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