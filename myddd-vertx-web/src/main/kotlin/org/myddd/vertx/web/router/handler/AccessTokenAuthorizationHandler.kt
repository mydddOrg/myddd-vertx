package org.myddd.vertx.web.router.handler

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BadAuthorizationException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.ext.execute
import org.myddd.vertx.web.router.ext.singleQueryParam
import java.util.*

class AccessTokenAuthorizationHandler(private val vertx: Vertx,val coroutineScope: CoroutineScope) : Handler<RoutingContext> {

    private val oAuth2Application: OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

    override fun handle(rc: RoutingContext?) {
        rc?.execute(coroutineScope){
            val accessToken = rc.singleQueryParam("accessToken")
            if(Objects.isNull(accessToken)) {
                throw BadAuthorizationException()
                return@execute
            }else{
                val clientId = oAuth2Application.queryValidClientIdByAccessToken(accessToken!!).await()
                clientId.also {
                    rc.put("accessToken",accessToken)
                    rc.put("clientId",clientId)
                    rc.next()
                }
            }

        }
    }

}