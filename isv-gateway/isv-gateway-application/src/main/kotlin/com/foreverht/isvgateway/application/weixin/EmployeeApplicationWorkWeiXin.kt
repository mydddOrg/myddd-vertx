package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.EmployeeApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.application.AbstractApplication
import io.vertx.core.Future

class EmployeeApplicationWorkWeiXin:AbstractApplication(),EmployeeApplication {

    override suspend fun queryEmployeeById(isvAccessToken: String, orgCode: String, userId: String): Future<EmployeeDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun batchQueryEmployeeByIds(isvAccessToken: String, orgCode: String, userIdList: List<String>): Future<List<EmployeeDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchEmployees(isvAccessToken: String, orgCode: String, query: String): Future<List<EmployeeDTO>> {
        TODO("Not yet implemented")
    }
}