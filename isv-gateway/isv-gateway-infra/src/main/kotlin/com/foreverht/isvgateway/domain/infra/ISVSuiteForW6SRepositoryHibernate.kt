package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ISVSuiteForW6S
import com.foreverht.isvgateway.domain.ISVSuiteForW6SRepository
import io.vertx.core.Future
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ISVSuiteForW6SRepositoryHibernate : EntityRepositoryHibernate(),ISVSuiteForW6SRepository {

    override suspend fun queryISVSuiteBySuiteKey(suiteKey: String): Future<ISVSuiteForW6S?> {
        return singleQuery(ISVSuiteForW6S::class.java,"from ISVSuiteForW6S w where w.suiteKey = :suiteKey", mapOf("suiteKey" to suiteKey))
    }
}