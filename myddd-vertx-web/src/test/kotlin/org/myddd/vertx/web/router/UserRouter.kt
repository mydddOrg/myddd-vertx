package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.web.SomethingErrorException
import org.myddd.vertx.web.SomethingErrorWithParamException
import org.myddd.vertx.web.WebErrorCode
import org.myddd.vertx.web.router.ext.jsonFormatEnd
import org.myddd.vertx.web.router.ext.singleQueryParam
import org.myddd.vertx.web.router.handler.AccessTokenAuthorizationHandler

class UserRouter(vertx: Vertx,router: Router) : AbstractRouter(vertx = vertx,router = router) {


    init {
        createUserGetRoute()
        createUserGetWithAuthorization()
        createUserPostRoute()
        createUserPutRoute()
        createUserPatchRoute()
        createUserDeleteRoute()
    }

    private fun createUserGetRoute(){
        createGetRoute("/$version/users"){ route ->
            route.handler {
                val error = it.queryParam("error")
                if(error.isNotEmpty()){
                    throw SomethingErrorException()
                }
                it.end()
            }
        }
    }

    private fun createUserGetWithAuthorization(){
        createGetRoute("/$version/authorization/users"){ route ->
            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                val error = it.queryParam("error")
                if(error.isNotEmpty()){
                    throw SomethingErrorException()
                }
                it.end()
            }
        }
    }

    private fun createUserPostRoute(){
        createPostRoute("/$version/users") { route ->
            route.handler(UserRouterValidation().postUserValidation())
            route.handler {
                val bodyJson = it.bodyAsJson
                val userId = bodyJson.getString("userId")

                val error = it.queryParam("error")
                if(error.isNotEmpty()){
                    throw SomethingErrorWithParamException(arrayOf(userId))
                }

                it.jsonFormatEnd(JsonObject().put("userId",userId).toBuffer())
            }
        }
    }

    private fun createUserPutRoute(){
        createPutRoute("/$version/users/:userId"){ route ->
            route.handler {
                val userId = it.pathParam("userId")
                val name = it.bodyAsJson.getString("name")

                val error = it.queryParam("error")
                if(error.isNotEmpty()){
                    throw SomethingErrorWithParamException(arrayOf(userId))
                }

                it.jsonFormatEnd(JsonObject().put("userId",userId).put("name",name).toBuffer())
            }
        }
    }

    private fun createUserPatchRoute(){
        createPatchRoute("/$version/users/:userId"){ route ->
            route.handler {
                val userId = it.pathParam("userId")
                val name = it.bodyAsJson.getString("name")

                val error = it.singleQueryParam("error")
                if(!error.isNullOrEmpty()){
                    throw SomethingErrorWithParamException(arrayOf(userId))
                }
                it.jsonFormatEnd(JsonObject().put("userId",userId).put("name",name.reversed()).toBuffer())
            }
        }
    }

    private fun createUserDeleteRoute(){
        createDeleteRoute("/$version/users/:userId"){ route ->
            route.handler {
                val userId = it.pathParam("userId")
                val error = it.singleQueryParam("error")
                if(!error.isNullOrEmpty()){
                    throw SomethingErrorWithParamException(arrayOf(userId))
                }

                it.response().setStatusCode(204).end()
            }
        }
    }

}