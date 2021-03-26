package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.EntityRepository
import java.io.Serializable
import java.util.*


open class EntityRepositoryHibernate : EntityRepository {

    protected val sessionFactory: Mutiny.SessionFactory by lazy { InstanceFactory.getInstance(Mutiny.SessionFactory::class.java) }

    override suspend fun <T : Entity> save(entity: T): Future<T> {
        val promise = PromiseImpl<T>()

        sessionFactory.withTransaction { session, _ ->
            session.find(entity::class.java,entity.getId())
                .chain { t -> if(Objects.isNull(t)) session.persist(entity) else session.merge(entity) }
                .chain { _ -> session.flush() }
        }.subscribe().with({
            promise.onSuccess(entity)
        },{
            promise.fail(it)
        })

        return promise.future()
    }

    override suspend fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?> {
        val promise = PromiseImpl<T>()
        sessionFactory.withSession { session ->
            session.find(clazz,id)
        }.subscribe().with({
            promise.onSuccess(it)
        },{
            promise.fail(it)
        })
        return promise.future()
    }

    override suspend fun <T : Entity> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        val promise = PromiseImpl<Boolean>()
        sessionFactory.withSession { session ->
            session.find(clazz,id)
        }.subscribe().with({
                findObj -> if(findObj != null) promise.onSuccess(true) else promise.onSuccess(false)},
            {
                promise.fail(it)
            }
        )
        return promise.future()
    }

    override suspend fun <T : Entity> batchSave(entityList:Array<T>): Future<Boolean> {
        val promise = PromiseImpl<Boolean>()
        sessionFactory.withTransaction { session, _ ->
            session.persistAll(*entityList).call { _ -> session.flush() }
        }.subscribe().with({promise.onSuccess(true)},{promise.fail(it)})
        return promise
    }

    override suspend fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        val promise = PromiseImpl<Boolean>()
        sessionFactory.withSession { session ->
            session.find(clazz,id).chain { it -> if(Objects.nonNull(it)) session.remove(it) else Uni.createFrom().nullItem()}.chain { _ -> session.flush() }
        }.subscribe().with({
            promise.onSuccess(true)
        },{
            promise.fail(it)}
        )
        return promise.future()
    }

    override suspend fun <T : Entity> listQuery(clazz: Class<T>?,sql: String,params: Map<String, Any>): Future<List<T>> {
        val promise = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(sql,clazz)
            params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.resultList
        }.subscribe().with({
            promise.onSuccess(it)
        },{
            promise.fail(it)
        })
        return promise.future()
    }

    override suspend fun <T : Entity> singleQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Future<T?> {
        val promise = PromiseImpl<T?>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(sql,clazz)
            params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.singleResultOrNull
        }.subscribe().with({
            promise.onSuccess(it)
        },{
            promise.fail(it)
        })
        return promise.future()
    }

    override suspend fun executeUpdate(sql: String,params: Map<String, Any>): Future<Int?> {
        val promise = PromiseImpl<Int?>()
        sessionFactory.withTransaction { session, _ ->

            val query = session.createQuery<Any>(sql)
            params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.executeUpdate().call { _ -> session.flush() }
        }.subscribe().with( {
            promise.onSuccess(it)
        },{
            promise.fail(it)
        })
        return promise.future()
    }

}