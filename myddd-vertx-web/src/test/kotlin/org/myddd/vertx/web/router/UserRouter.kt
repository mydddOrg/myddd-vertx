package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import org.myddd.vertx.base.BusinessLogicException

class UserRouter(vertx: Vertx,router: Router) : AbstractRouter(vertx = vertx,router = router) {




    init {
        createUserGetRoute()
        createUserPostRoute()
        createUserPutRoute()
        createUserPatchRoute()
        createUserDeleteRoute()
    }

    private fun createUserGetRoute(){
        createGetRoute("/$version/users"){
            val error = it.queryParam("error")
            if(error.isNotEmpty()){
                throw BusinessLogicException(WebErrorCode.SOMETHING_ERROR)
            }
            it.end()
        }
    }

    private fun createUserPostRoute(){
        createPostRoute("/$version/users",UserRouterValidation.postUserValidation()) {
            val bodyJson = it.bodyAsJson
            val userId = bodyJson.getString("userId")

            val error = it.queryParam("error")
            if(error.isNotEmpty()){
                throw BusinessLogicException(WebErrorCode.SOMETHING_ERROR_WITH_PARAM, arrayOf(userId))
            }

            it.end(JsonObject().put("userId",userId).toBuffer())
        }
    }

    private fun createUserPutRoute(){
        createPutRoute("/$version/users/:userId"){

            val userId = it.pathParam("userId")
            val name = it.bodyAsJson.getString("name")

            val error = it.queryParam("error")
            if(error.isNotEmpty()){
                throw BusinessLogicException(WebErrorCode.SOMETHING_ERROR_WITH_PARAM, arrayOf(userId))
            }

            it.end(JsonObject().put("userId",userId).put("name",name).toBuffer())
        }
    }

    private fun createUserPatchRoute(){
        createPatchRoute("/$version/users/:userId"){

            val userId = it.pathParam("userId")
            val name = it.bodyAsJson.getString("name")

            val error = it.queryParam("error")
            if(error.isNotEmpty()){
                throw BusinessLogicException(WebErrorCode.SOMETHING_ERROR_WITH_PARAM, arrayOf(userId))
            }


            it.end(JsonObject().put("userId",userId).put("name",name.reversed()).toBuffer())
        }
    }

    private fun createUserDeleteRoute(){
        createDeleteRoute("/$version/users/:userId"){
            val userId = it.pathParam("userId")
            val error = it.queryParam("error")
            if(error.isNotEmpty()){
                throw BusinessLogicException(WebErrorCode.SOMETHING_ERROR_WITH_PARAM, arrayOf(userId))
            }

            it.response().setStatusCode(204).end()
        }
    }

}