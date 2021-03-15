package org.myddd.vertx.web.router

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class NotExistsRouter(vertx: Vertx,router:Router) : AbstractRouter(vertx = vertx,router = router) {

    init {
        createNotExistsRouter()
    }

    private fun createNotExistsRouter(){
        router.route().order(Int.MAX_VALUE).respond { ctx ->
            val response = ctx.response()
            response.putHeader("content-type","application/json")
            response.statusCode = 404
            Future.succeededFuture(
                JsonObject()
                .put("errorCode", "API_NOT_FOUND"))
        }
    }
}