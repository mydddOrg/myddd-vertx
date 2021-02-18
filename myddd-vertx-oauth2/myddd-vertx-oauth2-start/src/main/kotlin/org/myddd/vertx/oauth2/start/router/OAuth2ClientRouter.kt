package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Handler
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
import org.myddd.vertx.domain.BusinessLogicException
import org.myddd.vertx.domain.ErrorCode
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.OAuth2WebErrorCode
import java.lang.Exception

class OAuth2ClientRouter constructor(router:Router,vertx:Vertx) : AbstractOAuth2Router(router,vertx) {

    private val oAuth2ClientApplication:OAuth2ClientApplication by lazy { InstanceFactory.getInstance(OAuth2ClientApplication::class.java) }

    init {
        createClientRoute()
    }

    private fun createClientRoute(){

        val handlers = ArrayList<Handler<RoutingContext>>()

        handlers.add(Handler<RoutingContext>{
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val body = it.bodyAsJson

                    if(body.getString("clientId").isNullOrEmpty() || body.getString("name").isNullOrEmpty()){
                        it.fail(BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_CREATE_CLIENT))
                    }

                    val createClientDTO = body.mapTo(OAuth2ClientDTO::class.java)

                    val created = oAuth2ClientApplication.createClient(createClientDTO).await()
                    val createdJson = JsonObject.mapFrom(created)
                    it.end(createdJson.toBuffer())
                } catch (e: Exception) {
                    it.fail(400, e)
                }
            }
        })

        createRoute(HttpMethod.POST,"/v1/oauth2/clients",handlers)
    }

}