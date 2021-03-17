package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.AbstractRouter

class ISVSuiteRoute(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {


    private val logger by lazy { LoggerFactory.getLogger(ISVSuiteRoute::class.java) }

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

    init {
        processCallbackRoute()
        querySuiteTicketRoute()
    }

    companion object {
        private const val EVENT_TYPE_SUITE_TICKET = "suite_ticket"

        private const val CLIENT_TYPE_WORKPLUS_ISV = "WorkPlusISV"
    }

    private fun processCallbackRoute(){
        createPostRoute(path = "/$version/w6s/isv"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    val bodyJson = it.bodyAsJson
                    when (bodyJson.getString("event_type")) {
                        EVENT_TYPE_SUITE_TICKET -> processSuiteTicketEvent(it)
                        else -> it.response().setStatusCode(404).end()
                    }
                    it.end(JsonObject().put("status",0).toBuffer())
                }

            }
        }.consumes(CONTENT_TYPE_JSON)
    }

    private fun querySuiteTicketRoute(){
        createGetRoute(path = "/$version/w6s/tickets/:suiteId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val isvSuiteTicketDTO = isvSuiteTicketApplication.querySuiteTicket(suiteId = suiteId,clientType = CLIENT_TYPE_WORKPLUS_ISV).await()

                        it.end(JsonObject.mapFrom(isvSuiteTicketDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }


                }
            }

        }
    }



    private suspend fun processSuiteTicketEvent(it:RoutingContext){
        try {
            val bodyJson = it.bodyAsJson
            val suiteKey = bodyJson.getString("suite_key")
            val suiteTicket = bodyJson.getJsonObject("param").getString("suite_ticket")
            isvSuiteTicketApplication.saveSuiteTicket(ISVSuiteTicketDTO(suiteId = suiteKey,suiteTicket = suiteTicket,clientType = CLIENT_TYPE_WORKPLUS_ISV))
        }catch (t:Throwable){
            it.fail(t)
        }
    }
}