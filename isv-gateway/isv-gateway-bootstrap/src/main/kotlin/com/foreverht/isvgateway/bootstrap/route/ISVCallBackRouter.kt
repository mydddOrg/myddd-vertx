package com.foreverht.isvgateway.bootstrap.route

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.AbstractRouter

class ISVCallBackRouter(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {


    private val logger by lazy { LoggerFactory.getLogger(ISVCallBackRouter::class.java) }

    init {
        receiveISVTicketRoute()
    }

    private fun receiveISVTicketRoute(){
        createPostRoute(path = "/isv/callback"){ route ->
            route.handler {
                logger.debug(it.bodyAsString)
                it.end(JsonObject().put("status",0).toBuffer())
            }
        }.consumes(CONTENT_TYPE_JSON)
    }

}