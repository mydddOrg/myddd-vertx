package org.myddd.vertx.web.router.ext

import io.vertx.ext.web.RoutingContext

fun RoutingContext.singleQueryParam(key:String,def:String? = null):String? {
    return if(this.queryParam(key).isNotEmpty()) this.queryParam(key)[0] else def
}