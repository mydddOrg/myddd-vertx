package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.AppDTO
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future

interface AppApplication {

    suspend fun getAdminList(isvAccessToken:String):Future<List<EmployeeDTO>>

    suspend fun getAppDetail(isvAccessToken:String):Future<AppDTO>

}