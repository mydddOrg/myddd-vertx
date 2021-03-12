package com.foreverht.isvgateway.api

import io.vertx.core.Future

interface MediaApplication {

    suspend fun uploadFile(clientId:String,path:String):Future<String>

    suspend fun downloadFile(clientId:String,mediaId:String):Future<String>
}