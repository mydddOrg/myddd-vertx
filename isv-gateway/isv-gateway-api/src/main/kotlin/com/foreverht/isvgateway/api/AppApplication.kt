package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.AppDTO
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future

interface AppApplication {

    suspend fun getAdminList(clientId:String):Future<List<EmployeeDTO>>

    suspend fun getAppDetail(clientId:String):Future<AppDTO>

}