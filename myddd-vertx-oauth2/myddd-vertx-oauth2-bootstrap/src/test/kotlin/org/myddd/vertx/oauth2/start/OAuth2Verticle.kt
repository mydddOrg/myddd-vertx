package org.myddd.vertx.oauth2.start

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.oauth2.start.router.OAuth2ClientRouter
import org.myddd.vertx.oauth2.start.router.OAuth2TokenRouter
import org.myddd.vertx.web.router.BootstrapVerticle

class OAuth2Verticle(private val port:Int = 8080) : BootstrapVerticle() {

    override suspend fun initIOC(vertx: Vertx) {
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            OAuth2ClientRouter(router = router,vertx = vertx)
            OAuth2TokenRouter(router = router,vertx = vertx)
        }
    }
}