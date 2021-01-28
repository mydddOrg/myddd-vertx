package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface OAuth2ClientRepository: EntityRepository {

    suspend fun queryClientByClientId(clientId:String): Future<OAuth2Client?>
}