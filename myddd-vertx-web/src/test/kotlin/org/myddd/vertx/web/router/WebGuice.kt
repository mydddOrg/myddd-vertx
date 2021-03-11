package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import org.hibernate.reactive.mutiny.Mutiny
import org.mockito.Mockito.mock
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

class WebGuice(private val vertx:Vertx) : AbstractModule() {

    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
        bind(WebClient::class.java).toInstance(WebClient.create(vertx))
        bind(I18N::class.java).to(I18NVertxProvider::class.java)

        bind(OAuth2Application::class.java).toInstance(mock(OAuth2Application::class.java))
    }
}