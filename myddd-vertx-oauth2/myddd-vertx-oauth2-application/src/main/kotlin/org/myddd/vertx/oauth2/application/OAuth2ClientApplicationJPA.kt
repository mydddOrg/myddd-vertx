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
        check(clientDTO.validForCreate()){
            "ID_VERSION_AND_SECRET_MUST_BE_NULL"
        }
        val oAuth2Client = toOAuth2Client(clientDTO)
        val created =  oAuth2Client.createClient().await()
        return Future.succeededFuture(toOAuth2ClientDTO(created))
    }

    override suspend fun queryClient(clientId: String): Future<OAuth2ClientDTO?> {
        val query = oAuth2ClientService.queryClientByClientId(clientId).await()
        query?.also {
            return Future.succeededFuture(toOAuth2ClientDTO(it!!))
        }
        return Future.succeededFuture(null)
    }

    override suspend fun resetClientSecret(clientId: String): Future<Boolean> {
        val query = oAuth2ClientService.queryClientByClientId(clientId).await()
        checkNotNull(query){
            "CLIENT_ID_NOT_EXISTS"
        }
        query.renewClientSecret().await()
        return Future.succeededFuture(true)
    }

    override suspend fun enableClient(clientId: String): Future<Boolean> {
        val query = oAuth2ClientService.queryClientByClientId(clientId).await()
        checkNotNull(query){
            "CLIENT_ID_NOT_EXISTS"
        }
        query.enable().await()
        return Future.succeededFuture(true)
    }

    override suspend fun disableClient(clientId: String): Future<Boolean> {
        val query = oAuth2ClientService.queryClientByClientId(clientId).await()
        checkNotNull(query){
            "CLIENT_ID_NOT_EXISTS"
        }
        query.disable().await()
        return Future.succeededFuture(true)
    }
}