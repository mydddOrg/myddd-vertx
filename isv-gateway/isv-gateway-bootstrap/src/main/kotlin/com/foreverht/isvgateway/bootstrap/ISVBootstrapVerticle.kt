package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.bootstrap.route.*
import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.BootstrapVerticle
import org.myddd.vertx.web.router.config.GlobalConfig

class ISVBootstrapVerticle (port:Int = 8080) : BootstrapVerticle(port = port){

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return ISVClientGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            AdminRoute(vertx,router)
            ISVClientRoute(vertx,router)
            OrganizationRoute(vertx,router)
            EmployeesRoute(vertx,router)
            MediaRoute(vertx,router)
            AppRoute(vertx,router)
            MessageRoute(vertx,router)
            StaticResourceRouter(vertx,router)

            ISVW6SSuiteRoute(vertx,router)
            WorkWeiXinRoute(vertx,router)

            if(!GlobalConfig.getBoolean("production",false)){
                MockRoute(vertx,router)
            }
        }
    }

}