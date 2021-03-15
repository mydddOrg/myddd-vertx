package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.bootstrap.route.*
import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.BootstrapVerticle

class ISVBootstrapVerticle (port:Int = 8080) : BootstrapVerticle(port = port){

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return ISVClientGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            ISVClientRouter(vertx,router)
            OrganizationRouter(vertx,router)
            EmployeesRouter(vertx,router)
            MediaRouter(vertx,router)
            AppRouter(vertx,router)
            MessageRouter(vertx,router)
            StaticResourceRouter(vertx,router)
        }
    }

}