package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Future

interface WorkWeiXinApplication {

    suspend fun requestSuiteAccessToken(clientId:String): Future<ISVClient>

    suspend fun requestPreAuthCode(clientId: String):Future<String>

    suspend fun setSessionInfo(clientId: String, productionMode:Boolean = false):Future<Unit>

    suspend fun activeAuth(clientId: String,suiteId:String,authCode:String):Future<ISVAuthCode>

    suspend fun requestCorpAccessToken(clientId: String,corpId:String):Future<ISVClientToken>

    suspend fun queryAgentId(corpAccessToken:String):Future<String>

}