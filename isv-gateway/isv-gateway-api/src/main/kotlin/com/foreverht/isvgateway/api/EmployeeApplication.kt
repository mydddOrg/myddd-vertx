package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future

interface EmployeeApplication {

    suspend fun queryEmployeeById(isvAccessToken:String, orgCode:String, userId:String):Future<EmployeeDTO>

    suspend fun batchQueryEmployeeByIds(isvAccessToken: String, orgCode: String, userIdList: List<String>): Future<List<EmployeeDTO>>

    suspend fun searchEmployees(isvAccessToken: String, orgCode: String, query:String):Future<List<EmployeeDTO>>
}