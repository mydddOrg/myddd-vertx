package org.myddd.vertx.querychannel.hibernate

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.*

class QueryChannelHibernate : QueryChannel {

    private val sessionFactory: Mutiny.SessionFactory by lazy { InstanceFactory.getInstance(Mutiny.SessionFactory::class.java) }

    override suspend fun  <T> pageQuery(queryParam: QueryParam<T>, pageParam: PageParam):Future<Page<T>> {
        val future = PromiseImpl<Page<T>>()
        this.pageQueryResult(queryParam,pageParam).onSuccess { list ->
            this.pageQueryCount(queryParam).onSuccess { totalCount ->
                future.onSuccess(Page(dataList = list,totalCount = totalCount,page = pageParam.page,pageSize = pageParam.pageSize))
            }
        }
        return future
    }

    private fun <T> pageQueryCount(queryParam: QueryParam<T>): Future<Long> {
        val future = PromiseImpl<Long>()
        sessionFactory.withSession { session ->
            val query = session.createQuery<Long>(queryParam.countSQL())
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.singleResult.invoke { count ->
                future.onSuccess(count)
            }
        }.await().indefinitely()
        return future
    }

    private fun <T> pageQueryResult(queryParam: QueryParam<T>, pageParam: PageParam): Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(queryParam.sql,queryParam.clazz)
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }

            query.setFirstResult(pageParam.page * pageParam.pageSize).setMaxResults(pageParam.pageSize).resultList.invoke { list ->
                future.onSuccess(list)
            }
        }.await().indefinitely()
        return future
    }

    override suspend fun <T> queryList(queryParam: QueryParam<T>):Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(queryParam.sql,queryParam.clazz)
            queryParam.params.forEach { (key, value) -> query.setParameter(key,value)  }

            query.resultList.invoke {
                list -> future.onSuccess(list)
            }
        }.await().indefinitely()
        return future
    }
}