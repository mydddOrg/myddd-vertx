package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future

interface EmployeeApplication {

    suspend fun queryEmployeeById(clientId:String,orgCode:String,userId:String):Future<EmployeeDTO>

    suspend fun batchQueryEmployeeByIds(clientId: String,orgCode: String,userIdList: List<String>): Future<List<EmployeeDTO>>

    suspend fun searchEmployees(clientId: String,orgCode: String,query:String):Future<List<EmployeeDTO>>
}