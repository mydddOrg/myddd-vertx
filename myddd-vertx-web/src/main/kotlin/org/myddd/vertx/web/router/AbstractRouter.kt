package org.myddd.vertx.web.router

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.BadRequestException
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.handler.IPFilterHandle

abstract class AbstractRouter constructor(protected val vertx: Vertx,protected val router:Router,protected val version:String = "v1") {

    private val bodyHandler = BodyHandler.create()

    private val errorI18n: I18N by lazy { InstanceFactory.getInstance(I18N::class.java) }


    companion object {
        const val ERROR_CODE = "errorCode"
        const val ERROR_MSG = "errorMsg"
        const val OTHER_ERROR = "OTHER_ERROR"
        const val BAD_REQUEST = "BAD_REQUEST"

        const val HTTP_400_RESPONSE = 400

        const val X_LANGUAGE_IN_HEADER = "X_LANGUAGE"

        const val CONTENT_TYPE_JSON = "application/json"
    }

    protected fun createGetRoute(path:String,handle: Handler<RoutingContext>): Route {
        return createRoute(HttpMethod.GET,path,handle)
    }

    protected fun createPostRoute(path:String,handle: Handler<RoutingContext>):Route {
        return createRoute(HttpMethod.POST,path,handle)
    }

    protected fun createPostRoute(path:String,validationHandler: ValidationHandler,handle: Handler<RoutingContext>):Route {
        return createRoute(HttpMethod.POST,path,validationHandler,handle)
    }

    protected fun createDeleteRoute(path:String,handle: Handler<RoutingContext>):Route {
        return createRoute(HttpMethod.DELETE,path,handle)
    }

    protected fun createPutRoute(path:String,handle: Handler<RoutingContext>):Route {
        return createRoute(HttpMethod.PUT,path,handle)
    }

    protected fun createPatchRoute(path:String,handle: Handler<RoutingContext>):Route {
        return createRoute(HttpMethod.PATCH,path,handle)
    }

    private fun createRoute(httpMethod: HttpMethod, path:String, handle: Handler<RoutingContext>):Route {
        val handles = listOf(handle)
        return createRoute(httpMethod,path,null,handles)
    }

    private fun createRoute(httpMethod: HttpMethod, path:String,validationHandler: ValidationHandler?,handle: Handler<RoutingContext>):Route {
        val handles = listOf(handle)
        return createRoute(httpMethod,path,validationHandler,handles)
    }

    private fun createRoute(httpMethod: HttpMethod, path:String,validationHandler: ValidationHandler?,handlers: List<Handler<RoutingContext>>):Route {
        val route = router.route(httpMethod,path)

        if(httpMethod != HttpMethod.DELETE && httpMethod != HttpMethod.GET){
            route.handler(bodyHandler)
            route.consumes(CONTENT_TYPE_JSON)
        }
        route.produces(CONTENT_TYPE_JSON)

        //enable ip filter
        route.handler(IPFilterHandle())

        //validation
        if(validationHandler!=null){
            route.handler(validationHandler)
        }

        handlers.forEach {
            route.handler(it)
        }

        failureHandler(route)

        return route
    }

    private fun failureHandler(route: Route) {
        route.failureHandler {
            GlobalScope.launch(vertx.dispatcher()) {
                val failure = it.failure()

                val language = it.request().getHeader(X_LANGUAGE_IN_HEADER)

                var responseJson = if (failure is BusinessLogicException) {
                    val errorMsgI18n =
                        errorI18n.getMessage(failure.errorCode.errorCode(), failure.values, language).await()

                    JsonObject()
                        .put(ERROR_CODE, failure.errorCode)
                        .put(ERROR_MSG, errorMsgI18n)
                } else if (failure is BadRequestException) {
                    JsonObject()
                        .put(ERROR_CODE, BAD_REQUEST)
                        .put(ERROR_MSG, failure.localizedMessage)
                } else {
                    JsonObject()
                        .put(ERROR_CODE, OTHER_ERROR)
                        .put(ERROR_MSG, failure.localizedMessage)
                }
                it.response().setStatusCode(HTTP_400_RESPONSE).end(responseJson.toBuffer())
            }
        }
    }

}