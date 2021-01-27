package org.myddd.vertx.oauth2.infra.repsitory

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.myddd.vertx.oauth2.domain.OAuth2Client
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class OAuth2ClientRepositoryHibernate :EntityRepositoryHibernate(), OAuth2ClientRepository{

    override suspend fun queryClientByClientId(clientId: String): Future<OAuth2Client?> {
        return singleQuery(OAuth2Client::class.java,"from OAuth2Client where clientId = :clientId", mapOf("clientId" to clientId))
    }
}