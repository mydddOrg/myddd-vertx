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

    override suspend fun queryAuthCode(suiteId: String, domainId: String, orgCode:String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return singleQuery(
            clazz = ISVAuthCode::class.java,
            sql = "from ISVAuthCode where suiteId = :suiteId and clientType = :clientType and orgCode = :orgCode and domainId = :domainId",
            params = mapOf(
                "suiteId" to suiteId,
                "domainId" to domainId,
                "clientType" to clientType,
                "orgCode" to orgCode
            )
        )
    }

    override suspend fun queryTemporaryAuthCode(suiteId: String,domainId:String, orgCode: String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return queryAuthCodeWithStatus(suiteId = suiteId,domainId = domainId,orgCode = orgCode,clientType = clientType,authStatus = ISVAuthStatus.Temporary)
    }

    override suspend fun queryPermanentAuthCode(suiteId: String, domainId:String,orgCode: String, clientType: ISVClientType): Future<ISVAuthCode?> {
        return queryAuthCodeWithStatus(suiteId = suiteId, domainId = domainId,orgCode = orgCode,clientType = clientType,authStatus = ISVAuthStatus.Permanent)
    }

    private suspend fun queryAuthCodeWithStatus(suiteId: String, domainId:String,orgCode:String, clientType: ISVClientType,authStatus: ISVAuthStatus): Future<ISVAuthCode?> {
        return singleQuery(
            clazz = ISVAuthCode::class.java,
            sql = "from ISVAuthCode where suiteId = :suiteId and clientType = :clientType and domainId = :domainId and orgCode = :orgCode and authStatus = :authStatus",
            params = mapOf(
                "suiteId" to suiteId,
                "clientType" to clientType,
                "orgCode" to orgCode,
                "domainId" to domainId,
                "authStatus" to authStatus
            )
        )
    }


}