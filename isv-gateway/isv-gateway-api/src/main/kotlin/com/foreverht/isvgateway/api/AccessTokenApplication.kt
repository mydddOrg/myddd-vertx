package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.RequestTokenDTO
import com.foreverht.isvgateway.api.dto.TokenDTO
import io.vertx.core.Future

interface AccessTokenApplication {

    suspend fun requestAccessToken(requestTokenDTO: RequestTokenDTO):Future<TokenDTO>

    suspend fun queryClientByAccessToken(isvAccessToken:String):Future<ISVClientDTO>

}