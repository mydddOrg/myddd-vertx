package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.EntityRepositoryUni
import java.io.Serializable
import java.util.*

open class EntityRepositoryHibernateUni(private val dataSource: String? = null):EntityRepositoryUni {

    protected val sessionFactory: Mutiny.SessionFactory by lazy {
        if(Objects.isNull(dataSource)) InstanceFactory.getInstance(Mutiny.SessionFactory::class.java)
        else InstanceFactory.getInstance(Mutiny.SessionFactory::class.java,dataSource)
    }

    override fun <T : Entity> persist(entity: T): Uni<T> {
        val session = SessionThreadLocal.get()
        entity.created = System.currentTimeMillis()
        return session.persist(entity).chain { it -> session.find(entity::class.java,entity.getId()) }
    }

    override fun <T : Entity> merge(entity: T): Uni<T> {
        val session = SessionThreadLocal.get()
        entity.updated = System.currentTimeMillis()
        return session.merge(entity).chain { it -> session.find(entity::class.java,it.getId()) }
    }

    override fun <T : Entity> save(entity: T): Uni<T> {
        val session = SessionThreadLocal.get()
        return session.find(entity::class.java, entity.getId())
            .chain { t ->
                if (Objects.isNull(t)) {
                    entity.created = System.currentTimeMillis()
                    session.persist(entity)
                } else {
                    entity.updated = System.currentTimeMillis()
                    session.merge(entity)
                }
            }
            .map { entity }
    }

    override fun <T : Entity> exists(clazz: Class<T>, id: Serializable): Uni<Boolean> {
        val session = SessionThreadLocal.get()
        return session.find(clazz, id)
            .chain { it ->
                if(Objects.nonNull(it)) Uni.createFrom().item(true) else Uni.createFrom().item(false)
            }
    }

    override fun <T : Entity> remove(entity: T): Uni<Unit> {
        return SessionThreadLocal.get().remove(entity).map {  }
    }

    override fun <T : Entity> get(clazz: Class<T>, id: Serializable): Uni<T?> {
        return SessionThreadLocal.get().find(clazz,id)
    }

    override fun <T : Entity> batchSave(entityList: Array<T>): Uni<Boolean> {
        return SessionThreadLocal.get().persistAll(*entityList)
            .map { true }
    }

    override fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Uni<Boolean> {
        val session = SessionThreadLocal.get()
        return session.find(clazz, id)
            .chain { it -> if (Objects.nonNull(it)) session.remove(it) else Uni.createFrom().nullItem() }
            .map { true }
    }

    override fun <T> singleQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Uni<T?> {
        val session = SessionThreadLocal.get()
        val query = session.createQuery(sql, clazz)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        return query.singleResultOrNull
    }

    override fun <T> listQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Uni<List<T>> {
        val session = SessionThreadLocal.get()
        val query = session.createQuery(sql, clazz)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        return query.resultList
    }

    override fun executeUpdate(sql: String, params: Map<String, Any>): Uni<Int> {
        val session = SessionThreadLocal.get()
        val query = session.createQuery<Any>(sql)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        return query.executeUpdate()
    }

    override fun <T> withTransaction(execution: () -> Uni<T>): Future<T> {
        val promise = PromiseImpl<T>()
        sessionFactory.withTransaction { session, _ ->
            SessionThreadLocal.set(session)
            execution()
                .invoke { it -> promise.onSuccess(it) }
        }.await().indefinitely()
        return promise.future()
    }
}