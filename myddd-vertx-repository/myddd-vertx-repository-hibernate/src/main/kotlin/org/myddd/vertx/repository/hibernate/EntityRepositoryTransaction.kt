package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.SessionObject

object EntityRepositoryTransaction {

    private val sessionFactory: SessionFactory by lazy {
        InstanceFactory.getInstance(SessionFactory::class.java)
    }

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val sessionFactoryMap:MutableMap<String,SessionFactory> = mutableMapOf()

    private fun getSessionFactory(dataSource: String? = null): SessionFactory{
        return if(dataSource.isNullOrEmpty()) sessionFactory
        else if(sessionFactoryMap.containsKey(dataSource)) sessionFactoryMap[dataSource]!!
        else {
            val dataSourceSessionFactory = InstanceFactory.getInstance(SessionFactory::class.java,dataSource)
            sessionFactoryMap[dataSource] = dataSourceSessionFactory
            dataSourceSessionFactory
        }
    }

    fun <T> withTransaction(work: java.util.function.Function<SessionObject, Uni<T>>): Future<T> {
        val promise = PromiseImpl<T>()
        val sessionFactory = getSessionFactory(null)
        sessionFactory.withTransaction { session, _ ->
            work.apply(MutinySessionObject.wrapper(session))
                .call { _ -> session.flush() }
        }.subscribe().with({ promise.onSuccess(it) },{ promise.fail(it) })
        return promise.future()
    }

    fun <T> withTransaction(dataSource: String,work: java.util.function.Function<SessionObject, Uni<T>>): Future<T> {
        val promise = PromiseImpl<T>()
        val sessionFactory = getSessionFactory(dataSource)
        sessionFactory.withTransaction { session, _ ->
            work.apply(MutinySessionObject.wrapper(session))
                .call { _ -> session.flush() }
        }.subscribe().with({ promise.onSuccess(it) },{ promise.fail(it) })
        return promise.future()
    }
}