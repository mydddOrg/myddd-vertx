package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.bootstrap.route.ISVClientRouter
import com.foreverht.isvgateway.bootstrap.route.OrganizationRouter
import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.BootstrapVerticle

class ISVBootstrapVerticle (private val port:Int = 8080) : BootstrapVerticle(port = port){

    private val logger by lazy { LoggerFactory.getLogger(ISVBootstrapVerticle::class.java) }

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return ISVClientGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            ISVClientRouter(vertx,router)
            OrganizationRouter(vertx,router)
        }
    }

}