package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class WebVerticle(port:Int = 8080) : BootstrapVerticle(port = port) {

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return WebGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return  {
            UserRouter(vertx,router)
        }
    }
}