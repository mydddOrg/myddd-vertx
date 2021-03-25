package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.bootstrap.validation.ISVClientValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.json.AsyncJsonMapper

class AdminRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    init {
        createISVClientRoute()
    }

    private fun createISVClientRoute(){
        createPostRoute("/$version/clients"){ route ->

            route.handler(ISVClientValidationHandler().createISVClientValidation())

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val bodyString = it.bodyAsString
                        val isvClientDTO = AsyncJsonMapper.mapFrom(vertx,bodyString, ISVClientDTO::class.java).await()
                        val created = isvClientApplication.createISVClient(isvClientDTO).await()
                        it.end(JsonObject.mapFrom(created).toBuffer())
                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE, t)
                    }
                }
            }
        }
    }

}