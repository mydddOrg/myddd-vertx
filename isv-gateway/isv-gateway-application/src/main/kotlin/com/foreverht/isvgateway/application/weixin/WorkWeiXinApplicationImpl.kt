package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVAuthStatus
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForWorkWeiXin
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
import io.vertx.core.Future
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class WorkWeiXinApplicationImpl:WorkWeiXinApplication {

    private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }
    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }
    private val logger by lazy { LoggerFactory.getLogger(WorkWeiXinApplication::class.java) }

    companion object {
        private const val WORK_WEI_XIN_API = "https://qyapi.weixin.qq.com/cgi-bin/service"
        private const val CLIENT_TYPE_WORK_WEI_XIN = "WorkWeiXin"
    }


    override suspend fun requestSuiteAccessToken(clientId: String): Future<ISVClient> {
        return try {
            val isvClient = ISVClient.queryClient(clientId = clientId).await()
            requireNotNull(isvClient){
                "clientId未找到"
            }

            val authExtra = isvClient.clientAuthExtra
            if(Objects.nonNull(authExtra) && authExtra!!.clientTokenValid()){
                Future.succeededFuture(isvClient)
            }else{
                val requested = requestSuiteAccessTokenFromRemote(isvClient).await()
                Future.succeededFuture(requested)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun requestPreAuthCode(clientId: String): Future<String> {
        return try {
            val isvClient = requestSuiteAccessToken(clientId = clientId).await()

            val extra = isvClient.clientAuthExtra as ISVClientAuthExtraForWorkWeiXin
            val response = webClient.getAbs("$WORK_WEI_XIN_API/get_pre_auth_code?suite_access_token=${extra.suiteAccessToken}")
                .send().await()
            if(response.statusCode() == 200){
                val body = response.bodyAsJsonObject()
                val preAuthCode = body.getString("pre_auth_code")
                Future.succeededFuture(preAuthCode)
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun setSessionInfo(clientId: String, productionMode:Boolean): Future<Unit> {
        return try {
            val isvClient = requestSuiteAccessToken(clientId = clientId).await()
            val extra = isvClient.clientAuthExtra as ISVClientAuthExtraForWorkWeiXin


            val preAuthCode = requestPreAuthCode(clientId = clientId).await()
            val authType = if(productionMode) 0 else 1
            val requestJson = json {
                obj(
                    "pre_auth_code" to preAuthCode,
                    "session_info" to obj(
                        "auth_type" to authType
                    )
                )
            }
            val response = webClient.postAbs("$WORK_WEI_XIN_API/set_session_info?suite_access_token=${extra.suiteAccessToken}")
                .sendJson(requestJson)
                .await()
            if(response.statusCode() == 200){
                Future.succeededFuture()
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun activeAuth(clientId: String, suiteId: String, authCode: String): Future<ISVAuthCode> {
        return try {
            val isvClient = requestSuiteAccessToken(clientId = clientId).await()
            val extra = isvClient.extra as ISVClientExtraForWorkWeiXin
            setSessionInfo(clientId = clientId,productionMode = false).await()
            val (permanentCode,corpId) = requestPermanentAuthCode(isvClient = isvClient,authCode = authCode).await()
            val isvAuthCode = ISVAuthCode.saveWorkWeiXinAuth(suiteId = extra.suiteId,orgCode = corpId,authCode = authCode,permanentCode = permanentCode).await()
            Future.succeededFuture(isvAuthCode)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    private suspend fun requestPermanentAuthCode(isvClient: ISVClient,authCode: String):Future<Pair<String,String>>{
        return try {
            val requestJson = json {
                obj(
                    "auth_code" to authCode
                )
            }
            val extra = isvClient.clientAuthExtra as ISVClientAuthExtraForWorkWeiXin

            val response = webClient.postAbs("$WORK_WEI_XIN_API/get_permanent_code?suite_access_token=${extra.suiteAccessToken}")
                .sendJson(requestJson)
                .await()
            logger.info("request permanent code")
            logger.info(response.bodyAsString())
            if(response.statusCode() == 200){
                val body  = response.bodyAsJsonObject()
                val permanentCode = body.getString("permanent_code")
                val corpId = body.getJsonObject("auth_corp_info").getString("corpid")
                Future.succeededFuture(Pair(permanentCode,corpId))
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun requestSuiteAccessTokenFromRemote(isvClient: ISVClient):Future<ISVClient>{
        return try {
            val clientExtra = isvClient.extra as ISVClientExtraForWorkWeiXin
            val suiteTicket = isvSuiteTicketApplication.querySuiteTicket(suiteId = clientExtra.suiteId,clientType = CLIENT_TYPE_WORK_WEI_XIN).await()

            val requestJson = json {
                obj(
                    "suite_id" to clientExtra.suiteId,
                    "suite_secret" to clientExtra.suiteSecret,
                    "suite_ticket" to suiteTicket.suiteTicket
                )
            }
            val response = webClient.postAbs("$WORK_WEI_XIN_API/get_suite_token")
                .sendJsonObject(requestJson)
                .await()

            if(response.statusCode() == 200){
                logger.debug(response.bodyAsString())
                val body = response.bodyAsJsonObject()
                val extra = ISVClientAuthExtraForWorkWeiXin.createInstanceFromJson(suiteAccessToken = body.getString("suite_access_token"),expiresIn = body.getLong("expires_in"))
                val updated = isvClient.saveClientAuthExtra(extra).await()
                Future.succeededFuture(updated)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}