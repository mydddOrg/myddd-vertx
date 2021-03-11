package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.BadRequestException
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BadAuthorizationException
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.handler.IPFilterHandler

abstract class AbstractRouter constructor(protected val vertx: Vertx,protected val router:Router,protected val version:String = "v1") {

    private val bodyHandler = BodyHandler.create()

    private val errorI18n: I18N by lazy { InstanceFactory.getInstance(I18N::class.java) }


    companion object {
        const val ERROR_CODE = "errorCode"
        const val ERROR_MSG = "errorMsg"
        const val OTHER_ERROR = "OTHER_ERROR"
        const val BAD_REQUEST = "BAD_REQUEST"
        const val BAD_AUTHORIZATION = "BAD_AUTHORIZATION"


        const val HTTP_400_RESPONSE = 400
        const val HTTP_403_RESPONSE = 403


        const val X_LANGUAGE_IN_HEADER = "X_LANGUAGE"

        const val CONTENT_TYPE_JSON = "application/json"
    }




    //------- width validation

    protected fun createGetRoute(path:String,validationHandler: ValidationHandler? = null,handlers: (route:Route) -> Unit):Route {
        return createRoute(HttpMethod.GET,path,validationHandler,handlers)
    }

    protected fun createPostRoute(path:String,validationHandler: ValidationHandler? = null ,handlers: (route:Route) -> Unit):Route {
        return createRoute(HttpMethod.POST,path,validationHandler,handlers)
    }

    protected fun createPatchRoute(path:String,validationHandler: ValidationHandler? = null,handlers: (route:Route) -> Unit):Route {
        return createRoute(HttpMethod.PATCH,path,validationHandler,handlers)
    }

    protected fun createDeleteRoute(path:String,validationHandler: ValidationHandler? = null, handlers: (route:Route) -> Unit):Route {
        return createRoute(HttpMethod.DELETE,path,validationHandler,handlers)
    }

    protected fun createPutRoute(path:String,validationHandler: ValidationHandler? = null, handlers: (route:Route) -> Unit):Route {
        return createRoute(HttpMethod.PUT,path,validationHandler,handlers)
    }




    private fun createRoute(httpMethod: HttpMethod, path:String, handlers: (route:Route) -> Unit):Route {
        return createRoute(httpMethod,path,null,handlers)
    }



    private fun createRoute(httpMethod: HttpMethod, path:String,validationHandler: ValidationHandler?,handlers: (route:Route) -> Unit):Route {
        val route = router.route(httpMethod,path)

        if(httpMethod != HttpMethod.DELETE && httpMethod != HttpMethod.GET){
            route.handler(bodyHandler)
            route.consumes(CONTENT_TYPE_JSON)
        }
        route.produces(CONTENT_TYPE_JSON)

        //enable ip filter
        route.handler(IPFilterHandler())

        //validation
        if(validationHandler!=null){
            route.handler(validationHandler)
        }

        handlers(route)

        failureHandler(route)

        return route
    }

    private fun failureHandler(route: Route) {
        route.failureHandler {
            GlobalScope.launch(vertx.dispatcher()) {
                val failure = it.failure()

                val language = it.request().getHeader(X_LANGUAGE_IN_HEADER)

                val (statusCode,responseJson) = when (failure) {
                    is BusinessLogicException -> {
                        val errorMsgI18n =
                            errorI18n.getMessage(failure.errorCode.errorCode(), failure.values, language).await()

                        val responseJson = JsonObject()
                            .put(ERROR_CODE, failure.errorCode)
                            .put(ERROR_MSG, errorMsgI18n)

                        Pair(HTTP_400_RESPONSE,responseJson)
                    }
                    is BadRequestException -> {
                        val responseJson = JsonObject()
                            .put(ERROR_CODE, BAD_REQUEST)
                            .put(ERROR_MSG, failure.localizedMessage)

                        Pair(HTTP_400_RESPONSE,responseJson)
                    }
                    is BadAuthorizationException -> {
                        val responseJson = JsonObject()
                            .put(ERROR_CODE, BAD_AUTHORIZATION)

                        Pair(HTTP_403_RESPONSE,responseJson)
                    }
                    else -> {
                        val responseJson = JsonObject()
                            .put(ERROR_CODE, OTHER_ERROR)
                            .put(ERROR_MSG, failure.localizedMessage)

                        Pair(HTTP_400_RESPONSE,responseJson)
                    }
                }

                it.response().setStatusCode(statusCode).end(responseJson.toBuffer())

            }
        }
    }

}