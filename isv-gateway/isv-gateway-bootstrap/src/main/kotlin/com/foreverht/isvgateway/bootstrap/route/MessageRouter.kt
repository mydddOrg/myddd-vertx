package com.foreverht.isvgateway.bootstrap.route

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import com.foreverht.isvgateway.bootstrap.handler.ISVAccessTokenAuthorizationHandler
import com.foreverht.isvgateway.bootstrap.validation.MessageValidationHandler
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageRouter(vertx: Vertx, router: Router):AbstractISVRouter(vertx = vertx,router = router) {

    init {
        sendMessageRoute()
    }

    private fun sendMessageRoute(){
        createPostRoute(path = "/$version/messages"){ route ->

            route.handler(MessageValidationHandler().messageValidationHandler())
            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")

                        val mapper = ObjectMapper().registerModule(KotlinModule())
                        val messageDTO = mapper.readValue(it.bodyAsString, MessageDTO::class.java)

                        val messageApplication = getMessageApplication(accessToken = accessToken).await()

                        messageApplication.sendMessage(isvAccessToken = accessToken,message = messageDTO).await()
                        it.response().setStatusCode(204).end()
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }


        }
    }

}