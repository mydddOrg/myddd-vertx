package com.foreverht.isvgateway.api

import io.vertx.core.Future

interface AccessTokenApplication {

    fun requestRequestAccessToken(clientId:String):Future<String>

}