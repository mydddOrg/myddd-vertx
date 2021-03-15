package com.foreverht.isvgateway.bootstrap.route

import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.web.router.handler.AccessTokenAuthorizationHandler

class AppRouter(vertx: Vertx, router: Router):AbstractISVRouter(vertx = vertx,router = router)  {

    init {
        queryAppDetailRoute()
        getAppAdminsRoute()
    }

    private fun queryAppDetailRoute(){
        createGetRoute(path = "/$version/app/detail"){ route ->

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val clientId = it.get<String>("clientId")

                        val appApplication = getAppApplication(accessToken = accessToken).await()
                        val appDTO = appApplication.getAppDetail(clientId = clientId).await()
                        it.end(JsonObject.mapFrom(appDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }
        }
    }

    private fun getAppAdminsRoute(){
        createGetRoute(path = "/$version/app/admins"){ route ->

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val clientId = it.get<String>("clientId")
                        val appApplication = getAppApplication(accessToken = accessToken).await()
                        val admins = appApplication.getAdminList(clientId = clientId).await()
                        it.end(JsonArray(admins.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }
        }
    }
}