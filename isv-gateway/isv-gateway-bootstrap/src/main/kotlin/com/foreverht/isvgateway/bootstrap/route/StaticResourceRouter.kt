package com.foreverht.isvgateway.bootstrap.route

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import org.myddd.vertx.web.router.AbstractRouter

class StaticResourceRouter(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {

    init {
        oasIndexRoute()
    }

    private fun oasIndexRoute(){
        router.route("/index.html").handler(StaticHandler.create());
    }


}