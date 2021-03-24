package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ProxyEmpOrgRelation
import com.foreverht.isvgateway.domain.ProxyRepository
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ProxyRepositoryHibernate: EntityRepositoryHibernate(),ProxyRepository {

    override suspend fun batchSaveEmpOrg(empOrgList: List<ProxyEmpOrgRelation>): Future<Unit> {
        return try {
            val promise = PromiseImpl<Unit>()
            sessionFactory.withTransaction{session, _ ->
                session.createQuery("delete from ProxyEmpOrgRelation",ProxyEmpOrgRelation::class.java).executeUpdate()
                session.persistAll(empOrgList.toTypedArray())
            }.subscribe().with({
                promise.onSuccess(Unit)
            }, {
                promise.fail(it)
            })
            return promise.future()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}