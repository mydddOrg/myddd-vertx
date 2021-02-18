package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import java.lang.Exception

class OAuth2ClientRouter constructor(router:Router,vertx:Vertx) {

    private val router:Router = router

    private val vertx:Vertx = vertx

    private val oAuth2ClientApplication:OAuth2ClientApplication by lazy { InstanceFactory.getInstance(OAuth2ClientApplication::class.java) }

    init {
        createClientRoute()
    }

    private fun createClientRoute(){
        val route = router.route(HttpMethod.POST,"/v1/oauth2/clients").consumes("application/json").produces("application/json")

        route.handler(BodyHandler.create());

        route.handler {
            GlobalScope.launch(vertx.dispatcher()) {
                createClientRouteExecutor(it)
            }
        }

        route.failureHandler {
            it.end()
        }
    }

    private suspend fun createClientRouteExecutor(it: RoutingContext) {
        try {
            val body = it.bodyAsJson
            val createClientDTO = body.mapTo(OAuth2ClientDTO::class.java)

            val created = oAuth2ClientApplication.createClient(createClientDTO).await()
            val createdJson = JsonObject.mapFrom(created)
            it.end(createdJson.toBuffer())
        } catch (e: Exception) {
            it.fail(400, e)
        }
    }

}