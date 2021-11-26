package org.myddd.vertx.oauth2.application

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.*
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2Token
import java.lang.Exception
import java.util.*

class OAuth2ApplicationJPA : OAuth2Application {

    private val clientService:OAuth2ClientService by lazy { InstanceFactory.getInstance(OAuth2ClientService::class.java) }

    override suspend fun requestClientToken(clientId: String, clientSecret: String): Future<OAuth2UserDTO?> {
        return try {
            val user = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(user)) throw ClientNotFoundException()

            if(user?.clientSecret != clientSecret) throw ClientSecretNotMatchException()

            if(user.disabled) throw ClientDisabledException()

            val token = clientService.generateClientToken(user).await()
            Future.succeededFuture(toOAuth2UserDTO(user,token))
        }catch (e:Exception){
            Future.failedFuture(e)
        }
    }

    override suspend fun refreshUserToken(clientId: String, refreshToken: String): Future<OAuth2UserDTO?> {

        return try {
            val queryUser = clientService.queryClientByClientId(clientId).await()

            if(Objects.isNull(queryUser))throw ClientNotFoundException()

            val token = clientService.refreshUserToken(queryUser!!,refreshToken).await()

            Future.succeededFuture(toOAuth2UserDTO(queryUser,token))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun revokeUserToken(clientId: String,accessToken:String): Future<Boolean> {
        return try {
            val queryUser = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(queryUser)) throw ClientNotFoundException()

            val queryToken = clientService.queryUserToken(clientId).await()
            if(Objects.isNull(queryToken)) throw ClientTokenNotFoundException()

            if(queryToken?.accessToken != accessToken) throw AccessTokenNotMatchException()

            clientService.revokeUserToken(queryUser!!).await()

            Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun loadUserToken(clientId: String): Future<OAuth2UserDTO?> {
        return try {
            val queryUser = clientService.queryClientByClientId(clientId).await()
            if(Objects.isNull(queryUser)) throw ClientNotFoundException()

            val queryToken = clientService.queryUserToken(clientId).await()

            if(Objects.isNull(queryToken)) throw ClientTokenNotFoundException()


            Future.succeededFuture(toOAuth2UserDTO(queryUser!!,queryToken!!))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryValidClientIdByAccessToken(accessToken: String): Future<String> {
        return try {
            Future.succeededFuture(OAuth2Token.queryValidClientIdByAccessToken(accessToken).await())
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}