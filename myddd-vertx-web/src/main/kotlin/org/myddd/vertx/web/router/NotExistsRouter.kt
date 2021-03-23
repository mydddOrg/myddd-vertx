package org.myddd.vertx.web.router

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class NotExistsRouter(vertx: Vertx,router:Router) : AbstractRouter(vertx = vertx,router = router) {

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(NotExistsRouter::class.java) }
    }
    init {
        createNotExistsRouter()
    }

    private fun createNotExistsRouter(){
        router.route().order(Int.MAX_VALUE).handler(BodyHandler.create()).respond { ctx ->
            logger.info("ERROR REQUEST URL: ${ctx.request().absoluteURI()}")
            logger.info(ctx.bodyAsString)

            logger.info(ctx.request().headers())
            val response = ctx.response()
            response.statusCode = 404
            Future.succeededFuture(
                JsonObject()
                .put("errorCode", "API_NOT_FOUND"))
        }.produces("application/json")
    }
}