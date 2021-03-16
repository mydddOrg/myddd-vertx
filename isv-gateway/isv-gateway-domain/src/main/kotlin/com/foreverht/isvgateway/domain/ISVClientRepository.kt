package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ISVClientRepository : EntityRepository {

    suspend fun createISVClient(isvClient: ISVClient): Future<ISVClient>

}