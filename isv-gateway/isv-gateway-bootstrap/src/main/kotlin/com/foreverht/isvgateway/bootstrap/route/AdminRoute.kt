package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.SyncDataApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.bootstrap.validation.ISVClientValidationHandler
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.json.AsyncJsonMapper
import org.myddd.vertx.web.router.ext.singleQueryParam

class AdminRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    private val syncDataApplication by lazy { InstanceFactory.getInstance(SyncDataApplication::class.java) }

    init {
        createISVClientRoute()
        syncDataRoute()
        listAllClients()
    }

    private fun listAllClients(){
        createGetRoute(path = "/$version/clients"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clients = isvClientApplication.listAllClients().await()
                        it.end(JsonArray(clients.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }
        }
    }


    private fun syncDataRoute(){
        createGetRoute(path = "/$version/sync/employeeAndOrganization/:clientId"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val domainId = it.singleQueryParam("domainId","WorkWeiXin")
                        val orgCode = it.singleQueryParam("orgCode")

                        requireNotNull(orgCode){
                            "orgCode不能为空"
                        }

                        syncDataApplication.syncOrganization(clientId = clientId,domainId = domainId!!,orgCode = orgCode!!).await()
                        it.end(JsonObject().put("result","SUCCESS").toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }

            }
        }
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