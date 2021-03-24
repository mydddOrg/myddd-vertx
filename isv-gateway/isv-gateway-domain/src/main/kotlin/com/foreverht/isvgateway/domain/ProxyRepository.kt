package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ProxyRepository: EntityRepository {

    suspend fun batchSaveEmpOrg(empOrgList:List<ProxyEmpOrgRelation>):Future<Unit>

}