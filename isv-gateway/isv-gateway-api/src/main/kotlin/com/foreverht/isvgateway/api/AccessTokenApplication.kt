package com.foreverht.isvgateway.api

import io.vertx.core.Future

interface AccessTokenApplication {

    suspend fun requestRequestAccessToken(clientId:String):Future<String?>

}