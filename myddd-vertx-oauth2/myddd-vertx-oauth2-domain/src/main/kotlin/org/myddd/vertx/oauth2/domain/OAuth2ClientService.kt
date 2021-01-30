package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import java.util.*
import javax.inject.Inject

class OAuth2ClientService {

    @Inject
    private lateinit var repository:OAuth2ClientRepository

    suspend fun queryUserToken(clientId:String):Future<OAuth2Token?> {
        val future = PromiseImpl<OAuth2Token>()
        var token = queryClientToken(clientId)
        future.onSuccess(token)
        return future
    }

    suspend fun queryClientByClientId(clientId:String):Future<OAuth2Client?> {
        return repository.queryClientByClientId(clientId)
    }

    suspend fun generateClientToken(client:OAuth2Client):Future<OAuth2Token>{
        val future = PromiseImpl<OAuth2Token>()
        var token = queryClientToken(client.clientId)
        if(Objects.isNull(token)) token = OAuth2Token.createTokenFromClient(client).await()
        future.onSuccess(token)
        return future
    }

    suspend fun refreshUserToken(client: OAuth2Client,refreshToken:String):Future<OAuth2Token>{
        val future = PromiseImpl<OAuth2Token>()
        var token = queryClientToken(client.clientId)

        check(token!=null){
            "TOKEN_NOT_EXISTS"
        }

        check(token.refreshToken == refreshToken){
            "REFRESH_TOKEN_NOT_MATCH"
        }

        token = if(Objects.isNull(token)) {
            OAuth2Token.createTokenFromClient(client).await()
        }else{
            token.refreshToken().await()
        }
        future.onSuccess(token)
        return future
    }

    suspend fun revokeUserToken(client: OAuth2Client):Future<Boolean>{
        val future = PromiseImpl<Boolean>()
        var token = queryClientToken(client.clientId)
        if(Objects.nonNull(token)){
            repository.delete(OAuth2Token::class.java,token!!.id).await()
        }
        future.onSuccess(true)
        return future
    }

    private suspend fun queryClientToken(clientId: String): OAuth2Token? {
        return repository.singleQuery(
            OAuth2Token::class.java,
            "from OAuth2Token where clientId = :clientId",
            mapOf("clientId" to clientId)
        ).await()
    }
}