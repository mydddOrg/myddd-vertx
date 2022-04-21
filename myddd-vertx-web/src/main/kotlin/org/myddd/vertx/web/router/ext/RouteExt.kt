package org.myddd.vertx.web.router.ext

import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Route.suspendHandler(coroutineScope: CoroutineScope,block:suspend (routingContext:RoutingContext) -> Unit):Route{
    this.handler {
        coroutineScope.launch {
            try {
                block(it)
            }catch (t:Throwable){
                it.fail(t)
            }
        }
    }
    return this
}