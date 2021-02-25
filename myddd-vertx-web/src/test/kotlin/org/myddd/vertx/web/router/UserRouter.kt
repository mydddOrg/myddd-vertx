package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

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
            it.end()
        }
    }

    private fun createUserPostRoute(){
        createPostRoute("/$version/users"){
            val bodyJson = it.bodyAsJson
            val userId = bodyJson.getString("userId")

            it.end(JsonObject().put("userId",userId).toBuffer())
        }
    }

    private fun createUserPutRoute(){
        createPutRoute("/$version/users/:userId"){
            val userId = it.pathParam("userId")
            val name = it.bodyAsJson.getString("name")
            it.end(JsonObject().put("userId",userId).put("name",name).toBuffer())
        }
    }

    private fun createUserPatchRoute(){
        createPatchRoute("/$version/users/:userId"){
            val userId = it.pathParam("userId")
            val name = it.bodyAsJson.getString("name")
            it.end(JsonObject().put("userId",userId).put("name",name.reversed()).toBuffer())
        }
    }

    private fun createUserDeleteRoute(){
        createDeleteRoute("/$version/users/:userId"){
            it.response().setStatusCode(204).end()
        }
    }

}