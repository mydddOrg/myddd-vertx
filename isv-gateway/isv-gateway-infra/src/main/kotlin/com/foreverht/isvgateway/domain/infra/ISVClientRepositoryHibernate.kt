package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteForW6S
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ISVClientRepositoryHibernate : EntityRepositoryHibernate(),ISVClientRepository {

    override suspend fun createISVClient(isvClient: ISVClient): Future<ISVClient> {
        return when (isvClient.clientType){
            ISVClientType.WorkPlusISV -> return createISVClientForWorkPlusISV(isvClient)
            else -> {
                save(isvClient)
            }
        }
    }


    private fun createISVClientForWorkPlusISV(isvClient: ISVClient):Future<ISVClient>{
        return try {
            val promise = PromiseImpl<ISVClient>()

            val extra = isvClient.extra as ISVClientExtraForWorkPlusISV
            val isvSuite = ISVSuiteForW6S.createInstanceFromClientExtra(extra)
            sessionFactory.withTransaction { session, _ ->
                session.persistAll(isvSuite,isvClient)
            }.subscribe().with({
                promise.onSuccess(isvClient)
            },{
                promise.fail(it)
            })
            return promise.future()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}