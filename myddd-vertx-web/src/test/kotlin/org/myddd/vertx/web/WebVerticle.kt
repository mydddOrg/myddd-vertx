package org.myddd.vertx.web

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.mockito.Mockito
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.BootstrapVerticle
import org.myddd.vertx.web.router.UserRouter

class WebVerticle(port:Int = 8080) : BootstrapVerticle(port = port) {

    companion object {
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure(){
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    bind(WebClient::class.java).toInstance(WebClient.create(vertx))
                    bind(I18N::class.java).to(I18NVertxProvider::class.java)

                    bind(OAuth2Application::class.java).toInstance(Mockito.mock(OAuth2Application::class.java))
                }
            }))
        }
    }

    override suspend fun initIOC(vertx: Vertx) {
        vertx.executeBlocking<Unit> {
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            it.complete()
        }.await()
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return  {
            UserRouter(vertx,router,this)
        }
    }
}