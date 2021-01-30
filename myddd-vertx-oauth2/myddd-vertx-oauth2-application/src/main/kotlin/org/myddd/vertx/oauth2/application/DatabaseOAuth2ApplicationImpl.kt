package org.myddd.vertx.oauth2.application

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.DatabaseOAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2Token
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

class DatabaseOAuth2ApplicationImpl : DatabaseOAuth2Application {

    private val clientService:OAuth2ClientService by lazy { InstanceFactory.getInstance(OAuth2ClientService::class.java) }

    override suspend fun validateClientUser(clientId: String, clientSecret: String): Future<OAuth2UserDTO?> {
        return try {
            val user = clientService.queryClientByClientId(clientId).await()
            checkNotNull(user){
                "CLIENT_NOT_FOUND"
            }
            check((user?.clientSecret == clientSecret)){
                "CLIENT_SECRET_NOT_MATCH"
            }
            check(!user!!.disabled){
                "CLIENT_DISABLED"
            }
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
            check(Objects.nonNull(queryUser)){
                RuntimeException("CLIENT_NOT_FOUND")
            }
            val token = clientService.refreshUserToken(queryUser!!,refreshToken).await()
            promise.onSuccess(toOAuth2UserDTO(queryUser,token))
        }catch (e:Exception){
            promise.fail(e)
        }

        return promise.future()
    }

    override suspend fun revokeUserToken(clientId: String): Future<Boolean> {
        val promise = PromiseImpl<Boolean>()

        try {
            val queryUser = clientService.queryClientByClientId(clientId).await()
            check(Objects.nonNull(queryUser)){
                "CLIENT_NOT_FOUND"
            }

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
            if(Objects.isNull(queryUser)){
                "CLIENT_ID_NOT_FOUND"
            }
            val queryToken = clientService.queryUserToken(clientId).await()
            if(Objects.isNull(queryToken)){
                "CLIENT_TOKEN_NOT_FOUND"
            }
            promise.onSuccess(toOAuth2UserDTO(queryUser!!,queryToken!!))
        }catch (e:Exception){
            promise.fail(e)
        }
        return promise.future()
    }
}