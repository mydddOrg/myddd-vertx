package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.OrganizationDTO
import io.vertx.core.Future

interface OrganizationApplication {

    suspend fun queryOrganizationById(orgId:String):Future<OrganizationDTO>

    suspend fun queryChildrenOrganizations(orgId: String):Future<List<OrganizationDTO>>

    suspend fun queryOrganizationEmployees(orgId: String):Future<List<OrganizationDTO>>

}