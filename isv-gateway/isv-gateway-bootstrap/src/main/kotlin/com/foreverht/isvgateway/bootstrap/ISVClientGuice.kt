package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.application.ISVClientApplicationImpl
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.application.OAuth2ApplicationJPA
import org.myddd.vertx.oauth2.application.OAuth2ClientApplicationJPA
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

class ISVClientGuice(private val vertx: Vertx) : AbstractModule() {

    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
        bind(I18N::class.java).to(I18NVertxProvider::class.java)

        bind(Mutiny.SessionFactory::class.java).toInstance(
            Persistence.createEntityManagerFactory("default")
                .unwrap(Mutiny.SessionFactory::class.java))

        bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)
        bind(OAuth2ClientService::class.java)
        bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
        bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))

        bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

        bind(OAuth2Application::class.java).to(OAuth2ApplicationJPA::class.java)
        bind(OAuth2ClientApplication::class.java).to(OAuth2ClientApplicationJPA::class.java)
        bind(I18N::class.java).to(I18NVertxProvider::class.java)


        bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
        bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)
    }

}