package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusApp
import io.vertx.core.Future
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.nio.Buffer
import java.util.*


class WorkPlusAccessTokenApplication : AccessTokenApplication{
    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }
    val logger: Logger by lazy { LoggerFactory.getLogger(WorkPlusAccessTokenApplication::class.java) }

    override suspend fun requestRequestAccessToken(clientId: String): Future<String?> {
        return try {
            val existsClientToken : ISVClientToken? = ISVClientToken.queryByClientId(clientId).await()
            if(Objects.nonNull(existsClientToken)){
                logger.info("命中缓存:${existsClientToken?.token}")
                Future.succeededFuture(existsClientToken!!.token)
            }else{
                val isvClient = ISVClient.queryClient(clientId).await()
                if(Objects.isNull(isvClient)){
                    throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
                }
                val tokenExtra = requestAccessTokenForISVClient(isvClient!!).await()

                saveISVClientToken(tokenExtra,clientId)
                Future.succeededFuture(tokenExtra.accessToken)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun saveISVClientToken(tokenExtra: ISVClientTokenExtraForWorkPlusApp,clientId: String) {
        try {
            ISVClientToken.saveByExtraToken(tokenExtra,clientId).await()
        } catch (t: Throwable) {
            logger.warn("保存TOKEN失败", t)
        }
    }

    private suspend fun requestAccessTokenForISVClient(isvClient: ISVClient):Future<ISVClientTokenExtraForWorkPlusApp> {
         return try {
             val extra = isvClient.extra as ISVClientExtraForWorkPlusApp
             val requestJSON = JsonObject()
                 .put("grant_type","client_credentials")
                 .put("scope","app")
                 .put("domain_id",extra.domainId)
                 .put("org_id",extra.ownerId)
                 .put("client_id",extra.clientId)
                 .put("client_secret",extra.clientSecret)

             val tokenResponse = webClient!!.postAbs("${extra.api}/token")
                 .sendJsonObject(requestJSON)
                 .await()

             val bodyJson = tokenResponse.bodyAsJsonObject()
             if(tokenResponse.resultSuccess()){
                 val result = bodyJson.getJsonObject("result")
                 logger.info("请求TOKEN的result:$result")
                 val tokenExtra = ISVClientTokenExtraForWorkPlusApp.createInstanceFormJsonObject(result)
                 Future.succeededFuture(tokenExtra)
             }else{
                 Future.failedFuture(bodyJson.toString())
             }
         }catch (t:Throwable){
             logger.error("请求远程TOKEN出错",t)
             Future.failedFuture(t)
         }
    }
}




