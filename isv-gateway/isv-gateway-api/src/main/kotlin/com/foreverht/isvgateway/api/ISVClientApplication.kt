package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import io.vertx.core.Future

interface ISVClientApplication {

    suspend fun queryClientInfo(clientId:String):Future<ISVClientDTO>

    suspend fun updateISVClient(isvClientDTO: ISVClientDTO):ISVClientDTO

    suspend fun createISVClient(isvClientDTO: ISVClientDTO):ISVClientDTO

}