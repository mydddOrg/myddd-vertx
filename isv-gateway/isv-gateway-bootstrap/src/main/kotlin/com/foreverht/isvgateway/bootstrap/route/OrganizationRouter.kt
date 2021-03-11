package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.OrganizationApplication
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.AbstractRouter
import org.myddd.vertx.web.router.handler.AccessTokenAuthorizationHandler

class OrganizationRouter(vertx: Vertx,router: Router):AbstractISVRouter(vertx = vertx,router = router) {

    private val organizationApplication:OrganizationApplication by lazy { InstanceFactory.getInstance(OrganizationApplication::class.java) }

    init {
        queryOrganizationRoute()
    }


    private fun queryOrganizationRoute(){
        createGetRoute("/$version/organizations"){ route ->
            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {

            }
        }
    }
}