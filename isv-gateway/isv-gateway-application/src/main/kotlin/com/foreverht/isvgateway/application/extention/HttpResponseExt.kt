package com.foreverht.isvgateway.application.extention

import io.vertx.ext.web.client.HttpResponse

fun <T> HttpResponse<T>.resultSuccess(): Boolean {
    val bodyJson = this.bodyAsJsonObject()
    return this.statusCode() == 200 && bodyJson?.getInteger("status") == 0
}

fun <T> HttpResponse<T>.resultSuccessForWorkWeiXin():Boolean {
    val bodyJson = this.bodyAsJsonObject()
    return this.statusCode() == 200 && bodyJson?.getInteger("errcode",0) == 0
}