package com.foreverht.isvgateway.api

import com.foreverht.isvgateway.api.dto.OrganizationDTO
import io.vertx.core.Future

interface OrganizationApplication {

    suspend fun queryOrganizationById(clientId:String,orgCode:String,orgId:String? = null):Future<OrganizationDTO>

    suspend fun queryChildrenOrganizations(clientId:String,orgCode:String,orgId: String? = null,limit:Int = 500, skip:Int = 0):Future<List<OrganizationDTO>>

    suspend fun queryOrganizationEmployees(clientId:String,orgCode:String,orgId: String? = null,limit:Int = 500, skip:Int = 0):Future<List<OrganizationDTO>>

}