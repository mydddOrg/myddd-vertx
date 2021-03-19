package com.foreverht.isvgateway.bootstrap.handler

import com.foreverht.isvgateway.api.AccessTokenApplication
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BadAuthorizationException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.ext.singleQueryParam
import java.util.*

class ISVAccessTokenAuthorizationHandler(private val vertx: Vertx) : Handler<RoutingContext> {

    private val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlusApp") }

    override fun handle(rc: RoutingContext?) {
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val accessToken = rc?.singleQueryParam("accessToken")
                if(Objects.isNull(accessToken)){
                    rc?.fail(BadAuthorizationException())
                    return@launch
                }
                val isvClientDTO = accessTokenApplication.queryClientByAccessToken(isvAccessToken = accessToken!!).await()

                isvClientDTO.also {
                    rc.put("accessToken",accessToken)
                    rc.put("clientId",isvClientDTO.clientId)
                    rc.next()
                }
            }catch (t:Throwable){
                rc?.fail(BadAuthorizationException())
            }
        }
    }

}