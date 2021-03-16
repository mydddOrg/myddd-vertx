package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteTicket
import io.vertx.core.Future
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ISVClientRepositoryHibernate : EntityRepositoryHibernate(),ISVClientRepository {

    override suspend fun querySuiteTicket(suiteId: String, clientType: ISVClientType): Future<ISVSuiteTicket?> {
        return singleQuery(
            clazz = ISVSuiteTicket::class.java,
            sql = "from ISVSuiteTicket where suiteId = :suiteId and clientType = :clientType",
            params = mapOf("suiteId" to suiteId,"clientType" to clientType)
        )
    }


}