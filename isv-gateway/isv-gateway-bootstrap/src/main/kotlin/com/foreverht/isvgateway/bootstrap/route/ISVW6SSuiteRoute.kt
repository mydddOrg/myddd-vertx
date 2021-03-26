package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.bootstrap.ext.jsonFormatEnd
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

class ISVW6SSuiteRoute(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {


    private val logger by lazy { LoggerFactory.getLogger(ISVW6SSuiteRoute::class.java) }

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

    private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }

    init {
        processCallbackRoute()
    }

    companion object {
        private const val EVENT_TYPE_SUITE_TICKET = "suite_ticket"

        private const val EVENT_TYPE_TMP_AUTH_CODE = "tmp_auth_code"

        private const val CLIENT_TYPE_WORKPLUS_ISV = "WorkPlusISV"
    }

    private fun processCallbackRoute(){
        createPostRoute(path = "/$version/callback/isv/:clientId"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    val bodyJson = it.bodyAsJson
                    logger.info("request body:")
                    logger.info(it.bodyAsString)
                    when (bodyJson.getString("event_type")) {
                        EVENT_TYPE_SUITE_TICKET -> processSuiteTicketEvent(it)
                        EVENT_TYPE_TMP_AUTH_CODE -> processTmpAuthCode(it)
                        else -> it.response().setStatusCode(404).end()
                    }
                }
            }
        }.consumes(CONTENT_TYPE_JSON)
    }

    private suspend fun processSuiteTicketEvent(it:RoutingContext){
        try {
            val bodyJson = it.bodyAsJson
            val suiteKey = bodyJson.getString("suite_key")
            val suiteTicket = bodyJson.getJsonObject("param").getString("suite_ticket")
            isvSuiteTicketApplication.saveSuiteTicket(ISVSuiteTicketDTO(suiteId = suiteKey,suiteTicket = suiteTicket,clientType = CLIENT_TYPE_WORKPLUS_ISV))
            it.jsonFormatEnd(JsonObject().put("status",0).toBuffer())
        }catch (t:Throwable){
            it.fail(t)
        }
    }

    private suspend fun processTmpAuthCode(it:RoutingContext){
        try {
            val bodyJson = it.bodyAsJson
            val param = bodyJson.getJsonObject("param")


            val clientId = it.pathParam("clientId")
            val domainId = param.getString("domain_id")
            val orgCode = param.getString("org_code")

            val isvAuthCodeDTO = ISVAuthCodeDTO(
                suiteId = bodyJson.getString("suite_key"),
                domainId = param.getString("domain_id"),
                orgCode = param.getString("org_code"),
                temporaryAuthCode = param.getString("tmp_auth_code"),
                clientType = CLIENT_TYPE_WORKPLUS_ISV,
                authStatus = "Temporary"
            )
            isvAuthCodeApplication.createTemporaryAuthCode(isvAuthCodeDTO).await()
            isvSuiteTicketApplication.activeSuite(clientId = clientId,domainId = domainId,orgCode = orgCode).await()
            it.jsonFormatEnd(JsonObject().put("status",0).toBuffer())
        }catch (t:Throwable){
            it.fail(t)
        }
    }
}