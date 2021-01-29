package org.myddd.vertx.oauth2.start

import com.google.inject.AbstractModule
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.oauth2.api.DatabaseOAuth2Application
import org.myddd.vertx.oauth2.application.DatabaseOAuth2ApplicationImpl
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import javax.persistence.Persistence

class MydddGuiceModule : AbstractModule() {

    override fun configure() {
        bind(Mutiny.SessionFactory::class.java).toInstance(
            Persistence.createEntityManagerFactory("default")
                .unwrap(Mutiny.SessionFactory::class.java))

        bind(EntityRepository::class.java).to(EntityRepositoryHibernate::class.java)
        bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)

        bind(OAuth2ClientService::class.java)
        bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
        bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))
        bind(DatabaseOAuth2Application::class.java).to(DatabaseOAuth2ApplicationImpl::class.java)
    }
}