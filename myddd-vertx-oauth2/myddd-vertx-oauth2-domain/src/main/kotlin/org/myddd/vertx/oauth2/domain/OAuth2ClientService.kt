package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.oauth2.AccessTokenNotExistsException
import org.myddd.vertx.oauth2.RefreshTokenNotMatchException
import java.util.*
import javax.inject.Inject

class OAuth2ClientService {

    @Inject
    private lateinit var repository:OAuth2ClientRepository

    suspend fun queryUserToken(clientId:String):Future<OAuth2Token?> {
        return try {
            queryClientToken(clientId = clientId)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun queryClientByClientId(clientId:String):Future<OAuth2Client?> {
        return repository.queryClientByClientId(clientId)
    }

    suspend fun generateClientToken(client:OAuth2Client):Future<OAuth2Token>{
        return try {
            var token = queryClientToken(client.clientId).await()

            if(Objects.isNull(token)) {
                token = OAuth2Token.createTokenFromClient(client).await()
            }
            Future.succeededFuture(token)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    suspend fun refreshUserToken(client: OAuth2Client,refreshToken:String):Future<OAuth2Token>{
        return try {
            val token = queryClientToken(client.clientId).await()

            if(Objects.isNull(token)) throw AccessTokenNotExistsException()

            if(token?.refreshToken != refreshToken) throw RefreshTokenNotMatchException()

            val refreshToken = token.refreshToken().await()

            Future.succeededFuture(refreshToken)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun revokeUserToken(client: OAuth2Client):Future<Boolean>{
        return try {
            val token = queryClientToken(client.clientId).await()
            if(Objects.nonNull(token)){
                repository.delete(OAuth2Token::class.java,token!!.id).await()
            }
            Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun queryClientToken(clientId: String): Future<OAuth2Token?> {
        return try {
            repository.singleQuery(
                clazz = OAuth2Token::class.java,
                sql = "from OAuth2Token where clientId = :clientId",
                params = mapOf("clientId" to clientId)
            )
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}