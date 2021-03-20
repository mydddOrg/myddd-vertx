package com.foreverht.isvgateway.application.workplus

import io.vertx.ext.web.client.HttpResponse

fun <T> HttpResponse<T>.resultSuccess(): Boolean {
    val bodyJson = this.bodyAsJsonObject()
    return this.statusCode() == 200 && bodyJson?.getInteger("status") == 0
}