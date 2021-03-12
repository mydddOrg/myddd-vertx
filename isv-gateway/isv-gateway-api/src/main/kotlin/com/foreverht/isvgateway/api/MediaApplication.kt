package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.MediaDTO
import io.vertx.core.Future

interface MediaApplication {

    suspend fun uploadFile(clientId:String,path:String):Future<String>

    suspend fun downloadFile(clientId:String,mediaId:String):Future<MediaDTO>
}