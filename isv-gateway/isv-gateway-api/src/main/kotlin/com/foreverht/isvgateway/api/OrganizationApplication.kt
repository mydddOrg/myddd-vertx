package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import io.vertx.core.Future

interface OrganizationApplication {

    suspend fun queryOrganizationById(clientId:String,orgCode:String,orgId:String? = null):Future<OrganizationDTO>

    suspend fun queryChildrenOrganizations(orgPageQueryDTO: OrgPageQueryDTO):Future<List<OrganizationDTO>>

    suspend fun queryOrganizationEmployees(orgPageQueryDTO: OrgPageQueryDTO):Future<List<EmployeeDTO>>

}