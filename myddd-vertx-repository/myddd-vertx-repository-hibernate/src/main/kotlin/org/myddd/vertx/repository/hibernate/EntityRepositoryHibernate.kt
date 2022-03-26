package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.EntityRepository
import java.io.Serializable
import java.util.*

open class EntityRepositoryHibernate(private val dataSource: String? = null) : EntityRepository {
    protected val sessionFactory: Mutiny.SessionFactory by lazy {
        if(Objects.isNull(dataSource))InstanceFactory.getInstance(Mutiny.SessionFactory::class.java)
        else InstanceFactory.getInstance(Mutiny.SessionFactory::class.java,dataSource)
    }

    override suspend fun <T : Entity> save(entity: T): Future<T> {
        return inTransaction { session ->
            session.find(entity::class.java, entity.getId())
                .chain { t ->
                    if (Objects.isNull(t)) {
                        entity.created = System.currentTimeMillis()
                        session.persist(entity)
                    } else {
                        entity.updated = System.currentTimeMillis()
                        session.merge(entity)
                    }
                }
                .call { _ -> session.flush() }
                .map { entity }
        }
    }

    override suspend fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?> {
        return inQuery { session ->
            session.find(clazz, id)
        }
    }

    override suspend fun <T : Entity> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        return inQuery { session ->
            session.find(clazz, id)
                .chain { it ->
                    if(Objects.nonNull(it)) Uni.createFrom().item(true) else Uni.createFrom().item(false)
                }
        }
    }

    override suspend fun <T : Entity> remove(entity: T): Future<Unit> {
        return inTransaction { session ->
            session.merge(entity).chain { it ->
                session.remove(it)
            }.map {  }
        }
    }

    override suspend fun <T : Entity> batchSave(entityList: Array<T>): Future<Boolean> {
        return inTransaction { session ->
            session.persistAll(*entityList)
                .map { true }
        }
    }

    override suspend fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        return inTransaction { session ->
            session.find(clazz, id)
                .chain { it -> if (Objects.nonNull(it)) session.remove(it) else Uni.createFrom().nullItem() }
                .chain { _ -> session.flush() }
                .map { true }
        }
    }

    override suspend fun <T> listQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Future<List<T>> {
        return inQuery { session ->
            val query = session.createQuery(sql, clazz)
            params.forEach { (key, value) -> query.setParameter(key, value) }
            query.resultList
        }
    }

    override suspend fun <T> singleQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Future<T?> {
        return inQuery { session ->
            val query = session.createQuery(sql, clazz)
            params.forEach { (key, value) -> query.setParameter(key, value) }
            query.singleResultOrNull
        }
    }

    override suspend fun executeUpdate(sql: String, params: Map<String, Any>): Future<Int?> {
        return inTransaction { session ->
            val query = session.createQuery<Any>(sql)
            params.forEach { (key, value) -> query.setParameter(key, value) }
            query.executeUpdate().call { _ -> session.flush() }
        }
    }

    fun <T> inTransaction(execution: (session: Session) -> Uni<T>): Future<T> {
        val promise = PromiseImpl<T>()
        sessionFactory.withTransaction { session, _ ->
            execution(session).invoke { it -> promise.onSuccess(it) }
        }.await().indefinitely()
        return promise.future()
    }

    fun <T> inQuery(execution: (session: Session) -> Uni<T>): Future<T> {
        val promise = PromiseImpl<T>()
        sessionFactory.withSession{ session ->
            execution(session).invoke { it -> promise.onSuccess(it) }
        }.await().indefinitely()
        return promise.future()
    }
}