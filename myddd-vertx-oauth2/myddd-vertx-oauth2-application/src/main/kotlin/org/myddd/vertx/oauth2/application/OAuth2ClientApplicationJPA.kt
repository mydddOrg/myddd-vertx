package org.myddd.vertx.oauth2.application

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.domain.OAuth2ClientService

class OAuth2ClientApplicationJPA : OAuth2ClientApplication {

    private val oAuth2ClientService by lazy { InstanceFactory.getInstance(OAuth2ClientService::class.java) }

    override suspend fun createClient(clientDTO: OAuth2ClientDTO): Future<OAuth2ClientDTO> {
        return try {
            check(clientDTO.validForCreate()){
                "ID_VERSION_AND_SECRET_MUST_BE_NULL"
            }
            val oAuth2Client = toOAuth2Client(clientDTO)
            val created =  oAuth2Client.createClient().await()
            Future.succeededFuture(toOAuth2ClientDTO(created))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun queryClient(clientId: String): Future<OAuth2ClientDTO?> {
        return try {
            val query = oAuth2ClientService.queryClientByClientId(clientId).await()
            query?.also {
                return Future.succeededFuture(toOAuth2ClientDTO(it))
            }
            Future.succeededFuture(null)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun resetClientSecret(clientId: String): Future<String> {
        return try {
            val query = oAuth2ClientService.queryClientByClientId(clientId).await()
            checkNotNull(query){
                "CLIENT_ID_NOT_EXISTS"
            }
            val reset = query.renewClientSecret().await()
            Future.succeededFuture(reset.clientSecret)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun enableClient(clientId: String): Future<Boolean> {
        return try {
            val query = oAuth2ClientService.queryClientByClientId(clientId).await()
            checkNotNull(query){
                "CLIENT_ID_NOT_EXISTS"
            }
            query.enable().await()
            Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun disableClient(clientId: String): Future<Boolean> {
        return try {
            val query = oAuth2ClientService.queryClientByClientId(clientId).await()
            checkNotNull(query){
                "CLIENT_ID_NOT_EXISTS"
            }
            query.disable().await()
            Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}