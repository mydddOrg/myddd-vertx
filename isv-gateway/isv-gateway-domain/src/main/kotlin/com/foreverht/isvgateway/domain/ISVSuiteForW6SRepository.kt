package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ISVSuiteForW6SRepository : EntityRepository {

    suspend fun queryISVSuiteBySuiteKey(suiteKey:String):Future<ISVSuiteForW6S?>
}