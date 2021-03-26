package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.bootstrap.ext.jsonFormatEnd
import com.foreverht.isvgateway.bootstrap.handler.ISVAccessTokenAuthorizationHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router)  {

    init {
        queryAppDetailRoute()
        getAppAdminsRoute()
    }

    private fun queryAppDetailRoute(){
        createGetRoute(path = "/$version/app/detail"){ route ->

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")

                        val appApplication = getAppApplication(accessToken = accessToken).await()
                        val appDTO = appApplication.getAppDetail(isvAccessToken = accessToken).await()
                        it.jsonFormatEnd(JsonObject.mapFrom(appDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }
        }
    }

    private fun getAppAdminsRoute(){
        createGetRoute(path = "/$version/app/admins"){ route ->

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val appApplication = getAppApplication(accessToken = accessToken).await()
                        val admins = appApplication.getAdminList(isvAccessToken = accessToken).await()
                        it.jsonFormatEnd(JsonArray(admins.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }
        }
    }
}