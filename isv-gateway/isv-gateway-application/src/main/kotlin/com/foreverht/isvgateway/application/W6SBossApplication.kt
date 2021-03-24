package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Future

interface W6SBossApplication {

    suspend fun requestISVToken(clientId: String):Future<ISVClient>

    suspend fun requestPermanentCode(clientId: String,domainId: String, orgCode:String):Future<ISVAuthCode>

    suspend fun activeSuite(clientId: String, domainId: String, orgCode: String):Future<Boolean>

    suspend fun requestApiAccessToken(clientId: String, domainId:String, orgCode: String): Future<ISVClientToken>

}