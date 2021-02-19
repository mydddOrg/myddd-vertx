package org.myddd.vertx.oauth2.application

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

class OAuth2ApplicationJPA : OAuth2Application {

    private val clientService:OAuth2ClientService by lazy { InstanceFactory.getInstance(OAuth2ClientService::class.java) }

    override suspend fun requestClientToken(clientId: String, clientSecret: String): Future<OAuth2UserDTO?> {
        return try {
            val user = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(user)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_NOT_FOUND)

            if(user?.clientSecret != clientSecret) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_SECRET_NOT_MATCH)

            if(user.disabled) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_DISABLED)

            val token = clientService.generateClientToken(user).await()
            Future.succeededFuture(toOAuth2UserDTO(user,token))
        }catch (e:Exception){
            Future.failedFuture(e)
        }
    }

    override suspend fun refreshUserToken(clientId: String, refreshToken: String): Future<OAuth2UserDTO?> {
        val promise = PromiseImpl<OAuth2UserDTO?>()

        try {
            val queryUser = clientService.queryClientByClientId(clientId).await()

            if(Objects.isNull(queryUser)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_NOT_FOUND)

            val token = clientService.refreshUserToken(queryUser!!,refreshToken).await()
            promise.onSuccess(toOAuth2UserDTO(queryUser,token))
        }catch (e:Exception){
            promise.fail(e)
        }

        return promise.future()
    }

    override suspend fun revokeUserToken(clientId: String,accessToken:String): Future<Boolean> {
        val promise = PromiseImpl<Boolean>()

        try {
            val queryUser = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(queryUser)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_NOT_FOUND)

            val queryToken = clientService.queryUserToken(clientId).await()
            if(Objects.isNull(queryToken)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_TOKEN_NOT_FOUND)

            if(queryToken?.accessToken != accessToken) throw BusinessLogicException(OAuth2ApiErrorCode.ACCESS_TOKEN_NOT_MATCH)

            clientService.revokeUserToken(queryUser!!).await()
            promise.onSuccess(true)
        }catch (e:Exception){
            promise.fail(e)
        }

        return promise.future()
    }

    override suspend fun loadUserToken(clientId: String): Future<OAuth2UserDTO?> {
        val promise = PromiseImpl<OAuth2UserDTO?>()
        try {
            val queryUser = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(queryUser)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_NOT_FOUND)

            val queryToken = clientService.queryUserToken(clientId).await()

            if(Objects.isNull(queryToken)) throw BusinessLogicException(OAuth2ApiErrorCode.CLIENT_TOKEN_NOT_FOUND)


            promise.onSuccess(toOAuth2UserDTO(queryUser!!,queryToken!!))
        }catch (e:Exception){
            promise.fail(e)
        }
        return promise.future()
    }
}