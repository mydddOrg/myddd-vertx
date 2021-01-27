package org.myddd.vertx.oauth2.infra.repsitory

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.myddd.vertx.oauth2.domain.OAuth2Client
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class OAuth2ClientRepositoryHibernate :EntityRepositoryHibernate(), OAuth2ClientRepository{

    override suspend fun queryClientByClientId(clientId: String): Future<OAuth2Client?> {
        val future = PromiseImpl<OAuth2Client?>()
        sessionFactory.withSession { session ->
            session.createQuery("from OAuth2Client where clientId = :clientId",OAuth2Client::class.java)
                .setParameter("clientId",clientId)
                .singleResult
                .invoke { client ->
                    future.onSuccess(client)
                }
        }.await().indefinitely()
        return future
    }
}