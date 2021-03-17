package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.*
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

    override suspend fun queryAuthCode(suiteId: String,orgId:String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return singleQuery(
            clazz = ISVAuthCode::class.java,
            sql = "from ISVAuthCode where suiteId = :suiteId and clientType = :clientType and orgId = :orgId",
            params = mapOf(
                "suiteId" to suiteId,
                "clientType" to clientType,
                "orgId" to orgId
            )
        )
    }

    override suspend fun queryTemporaryAuthCode(suiteId: String, orgId: String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return queryAuthCodeWithStatus(suiteId = suiteId,orgId = orgId,clientType = clientType,authStatus = ISVAuthStatus.Temporary)
    }

    override suspend fun queryPermanentAuthCode(suiteId: String, orgId: String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return queryAuthCodeWithStatus(suiteId = suiteId,orgId = orgId,clientType = clientType,authStatus = ISVAuthStatus.Permanent)
    }

    private suspend fun queryAuthCodeWithStatus(suiteId: String,orgId:String, clientType: ISVClientType,authStatus: ISVAuthStatus): Future<ISVAuthCode?> {
        return singleQuery(
            clazz = ISVAuthCode::class.java,
            sql = "from ISVAuthCode where suiteId = :suiteId and clientType = :clientType and orgId = :orgId and authStatus = :authStatus",
            params = mapOf(
                "suiteId" to suiteId,
                "clientType" to clientType,
                "orgId" to orgId,
                "authStatus" to authStatus
            )
        )
    }


}