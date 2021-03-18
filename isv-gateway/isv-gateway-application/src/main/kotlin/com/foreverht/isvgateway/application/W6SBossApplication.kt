package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Future

interface W6SBossApplication {

    suspend fun requestISVToken(clientId:String):Future<ISVClientToken?>

    suspend fun requestPermanentCode(clientId: String,orgId:String):Future<ISVAuthCode>

    suspend fun activeSuite(clientId: String,orgId: String):Future<Boolean>

    suspend fun requestApiAccessToken(clientId: String, orgId: String):Future<ISVAuthCode>
}