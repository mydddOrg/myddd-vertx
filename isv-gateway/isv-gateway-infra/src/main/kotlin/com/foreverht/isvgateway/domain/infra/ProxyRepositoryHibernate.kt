package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ProxyEmployee
import com.foreverht.isvgateway.domain.ProxyOrganization
import com.foreverht.isvgateway.domain.ProxyRepository
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ProxyRepositoryHibernate: EntityRepositoryHibernate(),ProxyRepository {

    override suspend fun syncEmployeeList(isvAuthCodeId: Long, employeeList: List<ProxyEmployee>): Future<Unit> {
        return try {
            val promise = PromiseImpl<Unit>()
            sessionFactory.withTransaction { session, _ ->
                session
                    .createQuery<ProxyEmployee>("delete from ProxyEmpOrgRelation where authCode = :authCodeId").setParameter("authCodeId",isvAuthCodeId)
                    .executeUpdate()
                    .chain { _->
                        session.createQuery<ProxyEmployee>("delete from ProxyEmployee where authCode.id = :authCodeId").setParameter("authCodeId",isvAuthCodeId)
                        .executeUpdate()
                    }
                    .chain { _ -> session.persistAll(*employeeList.toTypedArray()) }
                    .call { _ -> session.flush() }
            }.subscribe().with({
                promise.onSuccess(Unit)
            },{
                promise.fail(it)
            })
            return promise.future()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun syncOrganizationList(isvAuthCodeId: Long, organizationList: List<ProxyOrganization>): Future<Unit> {
        return try {
            val promise = PromiseImpl<Unit>()
            sessionFactory.withTransaction { session, _ ->
                session
                    .createQuery<ProxyEmployee>("delete from ProxyEmpOrgRelation where authCode = :authCodeId").setParameter("authCodeId",isvAuthCodeId)
                    .executeUpdate()
                    .chain { _ ->
                        session
                            .createQuery<ProxyEmployee>("delete from ProxyOrganization where authCode.id = :authCodeId").setParameter("authCodeId",isvAuthCodeId)
                            .executeUpdate()
                    }
                    .chain { _ -> session.persistAll(*organizationList.toTypedArray()) }
                    .call { _ -> session.flush() }
            }.subscribe().with({
                promise.onSuccess(Unit)
            },{
                promise.fail(it)
            })
            return promise.future()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}