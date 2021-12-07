package org.myddd.vertx.web

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.mockito.Mockito
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.api.OAuth2Application

class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object :AbstractModule(){
            override fun configure(){
                val vertx = Vertx.vertx()
                bind(Vertx::class.java).toInstance(vertx)
                bind(WebClient::class.java).toInstance(WebClient.create(vertx))
                bind(I18N::class.java).to(I18NVertxProvider::class.java)

                bind(OAuth2Application::class.java).toInstance(Mockito.mock(OAuth2Application::class.java))
            }
        })))
    }
}