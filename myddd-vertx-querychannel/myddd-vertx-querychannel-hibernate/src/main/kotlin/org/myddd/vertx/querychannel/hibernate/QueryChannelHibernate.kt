package org.myddd.vertx.querychannel.hibernate

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.Page
import org.myddd.vertx.querychannel.api.PageParam
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam

class QueryChannelHibernate : QueryChannel {

    private val sessionFactory: Mutiny.SessionFactory by lazy { InstanceFactory.getInstance(Mutiny.SessionFactory::class.java) }

    override suspend fun  <T> pageQuery(queryParam: QueryParam<T>, pageParam: PageParam):Future<Page<T>> {

        val future = PromiseImpl<Page<T>>()

        coroutineScope {
            val queryResult = withContext(Dispatchers.Default) {
                pageQueryResult(queryParam, pageParam)
            }
            var queryCount = withContext(Dispatchers.Default) {
                pageQueryCount(queryParam)
            }
            future.onSuccess(Page(dataList = queryResult.await(),totalCount = queryCount.await(),page = pageParam.page,pageSize = pageParam.pageSize))
        }
        return future
    }

    private fun <T> pageQueryCount(queryParam: QueryParam<T>): Future<Long> {
        val future = PromiseImpl<Long>()
        sessionFactory.withSession { session ->
            val query = session.createQuery<Long>(queryParam.countSQL())
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.singleResult
        }.subscribe().with({
            future.onSuccess(it)
        },{
            future.onFailure(it)
        })
        return future
    }

    private fun <T> pageQueryResult(queryParam: QueryParam<T>, pageParam: PageParam): Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(queryParam.sql,queryParam.clazz)
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }

            query.setFirstResult(pageParam.page * pageParam.pageSize).setMaxResults(pageParam.pageSize).resultList
        }.subscribe().with({
            future.onSuccess(it)
        },{
            future.onFailure(it)
        })
        return future
    }

    override suspend fun <T> queryList(queryParam: QueryParam<T>):Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(queryParam.sql,queryParam.clazz)
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.resultList
        }.subscribe().with({
            future.onSuccess(it)
        },{
            future.onFailure(it)
        })
        return future
    }
}