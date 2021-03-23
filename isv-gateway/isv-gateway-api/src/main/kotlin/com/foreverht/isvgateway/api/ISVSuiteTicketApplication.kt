package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import io.vertx.core.Future

interface ISVSuiteTicketApplication {

    suspend fun saveSuiteTicket(suiteTicket: ISVSuiteTicketDTO):Future<Boolean>

    suspend fun querySuiteTicket(suiteId:String,clientType:String):Future<ISVSuiteTicketDTO>

    suspend fun activeSuite(clientId: String, domainId: String, orgCode: String):Future<Unit>

    suspend fun activeAuthForWeiXin(clientId: String,suiteId: String,authCode:String):Future<Unit>

}