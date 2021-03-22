package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.RequestTokenDTO
import com.foreverht.isvgateway.api.TokenDTO
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.application.assembler.toISVClientDTO
import com.foreverht.isvgateway.application.workplus.AbstractApplicationWorkPlus
import com.foreverht.isvgateway.application.workplus.resultSuccess
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.*
import io.vertx.core.Future
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*


class AccessTokenApplicationImpl : AbstractApplicationWorkPlus(),AccessTokenApplication{

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    private val bossApplication:W6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }

    override suspend fun requestAccessToken(requestTokenDTO: RequestTokenDTO): Future<TokenDTO> {
        return try {
            val isvClientToken = ISVClientToken.queryClientToken(clientId = requestTokenDTO.clientId,domainId = requestTokenDTO.domainId,orgCode = requestTokenDTO.orgCode).await()
            if(Objects.nonNull(isvClientToken)){
                return when(isvClientToken!!.client.clientType){
                    ISVClientType.WorkPlusApp -> {
                        val extra = isvClientToken.extra as ISVClientTokenExtraForWorkPlusApp
                        Future.succeededFuture(TokenDTO(accessToken = isvClientToken.token,accessExpiredIn = extra.expireTime))
                    }
                    ISVClientType.WorkPlusISV -> {
                        val extra = isvClientToken.client.clientAuthExtra as ISVClientAuthExtraForISV
                        Future.succeededFuture(TokenDTO(accessToken = isvClientToken.token,accessExpiredIn = extra.expireTime))
                    }
                    else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
                }

            }else{
                val isvClient = ISVClient.queryClient(clientId = requestTokenDTO.clientId).await()
                requireNotNull(isvClient)
                return when(isvClient.clientType){
                    ISVClientType.WorkPlusApp -> requestFromRemoteForWorkPlusApp(requestTokenDTO)
                    ISVClientType.WorkPlusISV -> requestFromRemoteWorkPlusISV(requestTokenDTO)
                    else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
                }
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

    private suspend fun requestFromRemoteWorkPlusISV(requestTokenDTO: RequestTokenDTO):Future<TokenDTO> {
        return try {
            val isvClientToken = bossApplication.requestApiAccessToken(clientId = requestTokenDTO.clientId,domainId = requestTokenDTO.domainId,orgCode = requestTokenDTO.orgCode).await()
            val extra = isvClientToken.extra as ISVClientTokenExtraForWorkPlusISV
            Future.succeededFuture(TokenDTO(
                accessToken = isvClientToken.token,
                accessExpiredIn = extra.expireTime
            ))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun requestFromRemoteForWorkPlusApp(requestTokenDTO: RequestTokenDTO): Future<TokenDTO> {
        return try {
            val isvClient = ISVClient.queryClient(clientId = requestTokenDTO.clientId).await()
            val extra = isvClient!!.extra as ISVClientExtraForWorkPlusApp

            val requestJSON = json {
                obj(
                    "grant_type" to "client_credentials",
                    "scope" to "app",
                    "domain_id" to extra.domainId,
                    "org_id" to extra.ownerId,
                    "client_id" to extra.appKey,
                    "client_secret" to extra.appSecret
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




