package org.myddd.vertx.querychannel.hibernate

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.Page
import org.myddd.vertx.querychannel.api.PageQuery
import org.myddd.vertx.querychannel.api.QueryChannel

class QueryChannelHibernate : QueryChannel {

    private val sessionFactory: Mutiny.SessionFactory by lazy { InstanceFactory.getInstance(Mutiny.SessionFactory::class.java) }

    override fun <T> pageQuery(pageQuery: PageQuery<T>): Future<Page<T>> {
        val future = PromiseImpl<Page<T>>()
        this.pageQueryResult(pageQuery).onSuccess { list ->
            this.pageQueryCount(pageQuery).onSuccess { totalCount ->
                future.onSuccess(Page<T>(dataList = list,totalCount = totalCount,page = pageQuery.page,pageSize = pageQuery.pageSize))
            }
        }
        return future
    }

    private fun <T> pageQueryCount(pageQuery: PageQuery<T>): Future<Long> {
        val future = PromiseImpl<Long>()
        sessionFactory.withSession { session ->
            val query = session.createQuery<Long>(pageQuery.countSQL())
            pageQuery.params.forEach { param ->
                query.setParameter(pageQuery.params.indexOf(param),param)
            }
            query.singleResult.invoke { count ->
                future.onSuccess(count)
            }
        }.await().indefinitely()
        return future
    }

    private fun <T> pageQueryResult(pageQuery: PageQuery<T>): Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(pageQuery.sql,pageQuery.clazz)
            pageQuery.params.forEach { param ->
                query.setParameter(pageQuery.params.indexOf(param),param)
            }
            query.setFirstResult(pageQuery.page * pageQuery.pageSize).setMaxResults(pageQuery.pageSize).resultList.invoke { list ->
                future.onSuccess(list)
            }
        }.await().indefinitely()
        return future
    }

    override fun <T> queryList(clazz: Class<T>,sql: String, params: List<Any>): Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(sql,clazz)
            params.forEach { param ->
                query.setParameter(params.indexOf(param),param)
            }
            query.resultList.invoke {
                list -> future.onSuccess(list)
            }
        }.await().indefinitely()
        return future
    }
}