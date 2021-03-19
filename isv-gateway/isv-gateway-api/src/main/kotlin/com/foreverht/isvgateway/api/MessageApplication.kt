package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.message.MessageDTO
import io.vertx.core.Future

interface MessageApplication {
    suspend fun sendMessage(isvAccessToken:String, message:MessageDTO):Future<Boolean>
}