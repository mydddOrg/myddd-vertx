package org.myddd.vertx.web.router.handler

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BadAuthorizationException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import java.util.*

class AccessTokenAuthorizationHandler(private val vertx: Vertx) : Handler<RoutingContext> {

    private val oAuth2Application: OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

    override fun handle(rc: RoutingContext?) {
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val accessToken = rc?.queryParam("accessToken")?.get(0)
                if(Objects.isNull(accessToken)){
                    rc?.fail(BadAuthorizationException())
                    return@launch
                }
                val clientId = oAuth2Application.queryValidClientIdByAccessToken(accessToken!!).await()
                clientId.also {
                    rc.next()
                }
            }catch (t:Throwable){
                rc?.fail(BadAuthorizationException())
            }
        }
    }

}