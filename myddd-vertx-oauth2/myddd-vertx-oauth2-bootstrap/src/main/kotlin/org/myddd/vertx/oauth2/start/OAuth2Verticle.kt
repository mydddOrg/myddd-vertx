package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.start.router.OAuth2ClientRouter
import org.myddd.vertx.oauth2.start.router.OAuth2TokenRouter
import org.myddd.vertx.web.router.BootstrapVerticle

class OAuth2Verticle(private val port:Int = 8080) : BootstrapVerticle() {

    override suspend fun initIOC(vertx: Vertx) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(OAuth2GuiceModule(vertx))))
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            OAuth2ClientRouter(router = router,vertx = vertx, coroutineScope = this)
            OAuth2TokenRouter(router = router,vertx = vertx, coroutineScope = this)
        }
    }
}