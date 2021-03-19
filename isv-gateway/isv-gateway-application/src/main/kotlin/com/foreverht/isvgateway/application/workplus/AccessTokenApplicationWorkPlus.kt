package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.RequestTokenDTO
import com.foreverht.isvgateway.api.TokenDTO
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.application.assembler.toISVClientDTO
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusApp
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*


class AccessTokenApplicationWorkPlus : AbstractApplicationWorkPlus(),AccessTokenApplication{

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    override suspend fun requestAccessToken(requestTokenDTO: RequestTokenDTO): Future<TokenDTO> {
        return try {
            val isvClientToken = ISVClientToken.queryClientToken(clientId = requestTokenDTO.clientId,domainId = requestTokenDTO.domainId,orgCode = requestTokenDTO.orgCode).await()
            if(Objects.nonNull(isvClientToken)){
                val extra = isvClientToken!!.extra as ISVClientTokenExtraForWorkPlusApp
                Future.succeededFuture(TokenDTO(accessToken = isvClientToken!!.token,refreshToken = extra.refreshToken,accessExpiredIn = extra.expireTime))
            }else{
                requestFromRemote(requestTokenDTO)
            }

        }catch (t:Throwable){
            logger.error("请求远程TOKEN出错",t)
            Future.failedFuture(t)
        }
    }

    override suspend fun queryClientByAccessToken(isvAccessToken: String): Future<ISVClientDTO> {
        return try {
            val isvClientToken = ISVClientToken.queryByToken(token = isvAccessToken).await()
            if(Objects.nonNull(isvClientToken)){
                Future.succeededFuture(toISVClientDTO(isvClientToken!!.client))
            }else{
                throw BusinessLogicException(ISVErrorCode.ACCESS_TOKEN_INVALID)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    private suspend fun requestFromRemote(requestTokenDTO: RequestTokenDTO): Future<TokenDTO> {
        return try {
            val isvClient = ISVClient.queryClient(clientId = requestTokenDTO.clientId).await()
            val extra = isvClient!!.extra as ISVClientExtraForWorkPlusApp

            val requestJSON = json {
                obj(
                    "grant_type" to "client_credentials",
                    "scope" to "app",
                    "domain_id" to extra.domainId,
                    "org_id" to extra.ownerId,
                    "client_id" to extra.clientId,
                    "client_secret" to extra.clientSecret
                )
            }

            val tokenResponse = webClient.postAbs("${extra.api}/token")
                .sendJsonObject(requestJSON)
                .await()

            val bodyJson = tokenResponse.bodyAsJsonObject()
            return if (tokenResponse.resultSuccess()) {
                val result = bodyJson.getJsonObject("result")
                logger.info("请求TOKEN的result:$result")
                val tokenExtra = ISVClientTokenExtraForWorkPlusApp.createInstanceFormJsonObject(result)
                val isvClientToken = ISVClientToken.createInstanceByExtra(
                    client = isvClient,
                    extra = tokenExtra,
                    domainId = requestTokenDTO.domainId,
                    orgCode = requestTokenDTO.orgCode
                )
                val created = isvClientToken.createClientToken().await()


                Future.succeededFuture(
                    TokenDTO(
                        accessToken = created.token,
                        refreshToken = tokenExtra.refreshToken,
                        accessExpiredIn = tokenExtra.expireTime
                    )
                )
            } else {
                Future.failedFuture(bodyJson.toString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }
}




