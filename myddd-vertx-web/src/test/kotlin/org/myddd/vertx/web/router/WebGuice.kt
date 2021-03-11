package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import org.mockito.Mockito.mock
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.oauth2.api.OAuth2Application

class WebGuice(private val vertx:Vertx) : AbstractModule() {

    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
        bind(WebClient::class.java).toInstance(WebClient.create(vertx))
        bind(I18N::class.java).to(I18NVertxProvider::class.java)

        bind(OAuth2Application::class.java).toInstance(mock(OAuth2Application::class.java))
    }
}