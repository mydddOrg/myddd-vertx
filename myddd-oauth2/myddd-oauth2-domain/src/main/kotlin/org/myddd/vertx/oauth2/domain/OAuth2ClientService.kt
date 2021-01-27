package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import javax.inject.Inject

class OAuth2ClientService {

    @Inject
    private lateinit var repository:OAuth2ClientRepository

    suspend fun queryClientByClientId(clientId:String):Future<OAuth2Client?> {
        return repository.queryClientByClientId(clientId)
    }
}