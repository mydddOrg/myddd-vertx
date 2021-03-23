package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.domain.ISVClient
import io.vertx.core.Future

interface WorkWeiXinApplication {

    suspend fun requestSuiteAccessToken(clientId:String): Future<ISVClient>

    suspend fun requestPreAuthCode(clientId: String):Future<String>

    suspend fun setSessionInfo(clientId: String, productionMode:Boolean = false):Future<Unit>

}