package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.myddd.vertx.domain.BusinessLogicException

abstract class AbstractOAuth2Router constructor(router: Router, vertx: Vertx) {

    private val router:Router = router

    val vertx:Vertx = vertx

    val version = "v1"

    private val bodyHandler = BodyHandler.create()

    companion object {
        const val ERROR_CODE = "errorCode"
        const val ERROR_MSG = "errorMsg"
        const val OTHER_ERROR = "other error"

        const val HTTP_400_RESPONSE = 400
    }

    protected fun createRoute(httpMethod:HttpMethod,path:String,handle: Handler<RoutingContext>):Route {
        val handles = listOf(handle)
        return createRoute(httpMethod,path,handles)
    }

    protected fun createRoute(httpMethod:HttpMethod,path:String,handlers: List<Handler<RoutingContext>>):Route {
        val route = router.route(httpMethod,path).consumes("application/json").produces("application/json")
        route.handler(bodyHandler)

        handlers.forEach {
            route.handler(it)
        }

        route.failureHandler {
            val failure = it.failure()
            var responseJson= if(failure is BusinessLogicException){
                JsonObject()
                    .put(ERROR_CODE,failure.errorCode)
                    .put(ERROR_MSG,failure.errorMsg)
            }else{
                JsonObject()
                    .put(ERROR_CODE,OTHER_ERROR)
                    .put(ERROR_MSG,failure.localizedMessage)
            }

            it.response().setStatusCode(HTTP_400_RESPONSE).end(responseJson.toBuffer())
        }

        return route
    }

}