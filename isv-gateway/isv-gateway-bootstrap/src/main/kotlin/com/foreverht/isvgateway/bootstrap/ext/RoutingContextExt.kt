package com.foreverht.isvgateway.bootstrap.ext

import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext

fun RoutingContext.jsonFormatEnd(buffer:Buffer){
    this.response().putHeader("content-type","application/json;charset=utf-8")
    this.end(buffer)
}