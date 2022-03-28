package org.myddd.vertx.web

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.BootstrapVerticle
import org.myddd.vertx.web.router.UserRouter

class WebVerticle(port:Int = 8080) : BootstrapVerticle(port = port) {

    override suspend fun initIOC(vertx: Vertx) {
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return  {
            UserRouter(vertx,router,this)
        }
    }
}