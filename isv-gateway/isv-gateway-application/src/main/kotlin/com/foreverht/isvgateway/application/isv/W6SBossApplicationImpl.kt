package com.foreverht.isvgateway.application.isv

import com.foreverht.isvgateway.application.W6SBossApplication
import com.foreverht.isvgateway.application.workplus.resultSuccess
import com.foreverht.isvgateway.domain.*
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForISV
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
        private const val EXPIRE_TIME = "expire_time"
        private const val ACCESS_TOKEN = "access_token"
    }

    override suspend fun requestISVToken(clientId: String): Future<ISVClient> {
        return try {

            val isvClient = queryExistClient(clientId).await()

            val clientAuthExtra = isvClient.clientAuthExtra as ISVClientAuthExtraForISV?

            if (Objects.nonNull(clientAuthExtra) && clientAuthExtra!!.clientTokenValid()){
                Future.succeededFuture(isvClient)
            }
            else{
                val generated = generateToken(isvClient).await()
                Future.succeededFuture(generated)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun requestPermanentCode(clientId: String, domainId: String, orgCode: String): Future<ISVAuthCode> {
        return try {
            val isvClient = requestISVToken(clientId).await()
            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV
            val clientAuthExtra = isvClient.clientAuthExtra as ISVClientAuthExtraForISV

            val authCode = ISVAuthCode.queryAuthCode(suiteId = extra.suiteKey,domainId = domainId,orgCode = orgCode,clientType = ISVClientType.WorkPlusISV).await()
            if(authCode?.authStatus == ISVAuthStatus.Permanent){
                Future.succeededFuture(authCode)
            }else{
                val requestBody = json {
                    obj(
                        SUITE_KEY to extra.suiteKey,
                        TEMPORARY_AUTH_CODE to authCode!!.temporaryAuthCode
                    )
                }
                val requestUrl = String.format(PERMANENT_URL,extra.isvApi, clientAuthExtra.accessToken)
                val response = webClient.postAbs(requestUrl)
                    .sendJsonObject(requestBody)
                    .await()
                if(response.resultSuccess()){
                    val body = response.bodyAsJsonObject()
                    authCode!!.permanentAuthCode = body.getJsonObject("result").getString("permanent_code")
                    val permanentCode = authCode.toPermanent().await()
                    Future.succeededFuture(permanentCode)
                }else{
                    Future.failedFuture(response.bodyAsString())
                }
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun activeSuite(clientId: String, domainId: String, orgCode: String): Future<Boolean> {
        return try {
            val (isvClient,permanentAuthCode) = queryPermanentSuite(clientId = clientId,domainId = domainId,orgCode = orgCode).await()
            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV
            val clientAuthExtra = isvClient.clientAuthExtra as ISVClientAuthExtraForISV?
            requireNotNull(clientAuthExtra)

            val requestUrl = String.format(ACTIVATE_SUITE_URL,extra.isvApi, clientAuthExtra.accessToken)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(
                    json {
                        obj(
                            SUITE_KEY to extra.suiteKey,
                            PERMANENT_CODE to permanentAuthCode.permanentAuthCode
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

    override suspend fun requestApiAccessToken(clientId: String, domainId:String, orgCode: String): Future<ISVClientToken> {
        return try {
            val (isvClient,permanentAuthCode) = queryPermanentSuite(clientId = clientId,domainId = domainId,orgCode = orgCode).await()
            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV
            val clientAuthExtra = isvClient.clientAuthExtra as ISVClientAuthExtraForISV?
            requireNotNull(clientAuthExtra)

            val requestJson = json {
                obj(
                    SUITE_KEY to extra.suiteKey,
                    PERMANENT_CODE to permanentAuthCode.permanentAuthCode,
                    DOMAIN_ID to permanentAuthCode.domainId,
                    ORG_ID to permanentAuthCode.orgCode,
                )
            }
            val requestUrl = String.format(API_ACCESS_TOKEN,extra.isvApi,clientAuthExtra.accessToken)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(requestJson)
                .await()

            if(response.resultSuccess()){
                val body = response.bodyAsJsonObject()
                val clientTokenExtra = ISVClientTokenExtraForWorkPlusISV.createInstanceFromJson(body.getJsonObject("result"))
                val isvClientToken = ISVClientToken.createInstanceByExtra(client = isvClient,domainId = domainId,orgCode = orgCode,extra = clientTokenExtra)

                val created = isvClientToken.createClientToken().await()
                Future.succeededFuture(created)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun queryExistClient(clientId: String):Future<ISVClient>{
        return try {

            val isvClient = ISVClient.queryClient(clientId = clientId).await()
            if(Objects.isNull(isvClient)){
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }
            Future.succeededFuture(isvClient)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun queryPermanentSuite(clientId: String, domainId: String, orgCode: String):Future<Pair<ISVClient,ISVAuthCode>>{
        return try {

            val isvClient = requestISVToken(clientId).await()

            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV

            val permanentAuthCode = ISVAuthCode.queryPermanentAuthCode(suiteId = extra.suiteKey,domainId = domainId, orgCode = orgCode,clientType = ISVClientType.WorkPlusISV).await()
            if(Objects.isNull(permanentAuthCode)){
                throw BusinessLogicException(ISVErrorCode.PERMANENT_CODE_NOT_FOUND)
            }

            Future.succeededFuture(Pair(isvClient,permanentAuthCode!!))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun generateToken(isvClient: ISVClient): Future<ISVClient> {
        return try {
            val (accessToken, expireTime) = requestSuiteToken(isvClient = isvClient).await()
            val clientAuthExtra = ISVClientAuthExtraForISV.createInstance(accessToken = accessToken, expireTime = expireTime)
            val updated = isvClient.saveClientAuthExtra(clientAuthExtra).await()
            return Future.succeededFuture(updated)
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
                Future.succeededFuture(Pair(result.getString(ACCESS_TOKEN),result.getLong(EXPIRE_TIME)))
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}