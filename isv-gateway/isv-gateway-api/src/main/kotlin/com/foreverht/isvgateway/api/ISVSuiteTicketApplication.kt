package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import io.vertx.core.Future

interface ISVSuiteTicketApplication {

    suspend fun saveSuiteTicket(suiteTicket: ISVSuiteTicketDTO):Future<Boolean>

    suspend fun querySuiteTicket(suiteId:String,clientType:String):Future<ISVSuiteTicketDTO>
}