package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import io.vertx.core.Future

interface ISVAuthCodeApplication {

    suspend fun createTemporaryAuthCode(authCode:ISVAuthCodeDTO):Future<ISVAuthCodeDTO?>

    suspend fun toPermanent(authCode:ISVAuthCodeDTO):Future<ISVAuthCodeDTO?>

    suspend fun queryTemporaryAuthCode(suiteId:String,domainId:String, orgCode:String, clientType:String):Future<ISVAuthCodeDTO?>

    suspend fun queryPermanentAuthCode(suiteId: String,domainId: String, orgCode:String, clientType: String):Future<ISVAuthCodeDTO?>

}