package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import io.vertx.core.Future

interface ISVClientApplication {

    suspend fun queryClientByClientId(clientId:String):Future<ISVClientDTO?>

    suspend fun updateISVClient(isvClientDTO: ISVClientDTO):Future<ISVClientDTO>

    suspend fun createISVClient(isvClientDTO: ISVClientDTO):Future<ISVClientDTO>

}