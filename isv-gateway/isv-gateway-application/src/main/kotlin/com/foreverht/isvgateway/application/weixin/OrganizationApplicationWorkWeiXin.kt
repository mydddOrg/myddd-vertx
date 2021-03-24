package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.application.AbstractApplication
import io.vertx.core.Future

class OrganizationApplicationWorkWeiXin: AbstractApplication(),OrganizationApplication {

    override suspend fun queryOrganizationById(isvAccessToken: String, orgCode: String, orgId: String?): Future<OrganizationDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun queryChildrenOrganizations(orgPageQueryDTO: OrgPageQueryDTO): Future<List<OrganizationDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun queryOrganizationEmployees(orgPageQueryDTO: OrgPageQueryDTO): Future<List<EmployeeDTO>> {
        TODO("Not yet implemented")
    }
}