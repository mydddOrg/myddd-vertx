package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ProxyRepository: EntityRepository {

    suspend fun syncEmployeeList(isvAuthCodeId:Long,employeeList:List<ProxyEmployee>):Future<Unit>

    suspend fun syncOrganizationList(isvAuthCodeId:Long,organizationList:List<ProxyOrganization>):Future<Unit>
}