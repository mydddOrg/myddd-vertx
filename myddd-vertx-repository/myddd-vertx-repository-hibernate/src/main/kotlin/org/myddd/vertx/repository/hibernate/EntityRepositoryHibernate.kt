package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.api.EntityRepositoryUni
import java.io.Serializable
import java.util.*

open class EntityRepositoryHibernate(private val dataSource: String? = null) : EntityRepository,EntityRepositoryUni {

    protected val sessionFactory: Mutiny.SessionFactory by lazy {
        if(Objects.isNull(dataSource))InstanceFactory.getInstance(Mutiny.SessionFactory::class.java)
        else InstanceFactory.getInstance(Mutiny.SessionFactory::class.java,dataSource)
    }

    private val logger by lazy { LoggerFactory.getLogger(EntityRepositoryHibernate::class.java) }

    override suspend fun <T : Entity> save(entity: T): Future<T> {
        return inTransaction { session ->
            logger.debug("SAVE:" + Thread.currentThread().name)
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
                .map { entity }
        }
    }

    override suspend fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?> {
        return inQuery { session ->
            logger.debug("GET:" + Thread.currentThread().name)
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
            query.executeUpdate()
        }
    }

    fun <T> inTransaction(execution: (session: Session) -> Uni<T>): Future<T> {
        val promise = PromiseImpl<T>()
        sessionFactory.withTransaction { session, _ ->
            execution(session).call { _ -> session.flush() }
        }.subscribe().with({ promise.onSuccess(it)},{ promise.fail(it)})
        return promise.future()
    }

    fun <T> inQuery(execution: (session: Session) -> Uni<T>): Future<T> {
        val promise = PromiseImpl<T>()

        sessionFactory.withSession{ session ->
            execution(session)
        }.subscribe().with({ promise.onSuccess(it)},{ promise.fail(it)})
        return promise.future()
    }

    override fun <T : Entity> persistUni(entity: T): Uni<T> {
        val session = SessionThreadLocal.get()
        entity.created = System.currentTimeMillis()
        return session.persist(entity).chain { _ -> session.find(entity::class.java,entity.getId()) }
    }

    override fun <T : Entity> mergeUni(entity: T): Uni<T> {
        val session = SessionThreadLocal.get()
        entity.updated = System.currentTimeMillis()
        return session.merge(entity).chain { it -> session.find(entity::class.java,it.getId()) }
    }

    override fun <T : Entity> saveUni(entity: T): Uni<T> {
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

    override fun <T : Entity> existsUni(clazz: Class<T>, id: Serializable): Uni<Boolean> {
        val session = SessionThreadLocal.get()
        return session.find(clazz, id)
            .chain { it ->
                if(Objects.nonNull(it)) Uni.createFrom().item(true) else Uni.createFrom().item(false)
            }
    }

    override fun <T : Entity> removeUni(entity: T): Uni<Unit> {
        return SessionThreadLocal.get().remove(entity).map {  }
    }

    override fun <T : Entity> getUni(clazz: Class<T>, id: Serializable): Uni<T?> {
        return SessionThreadLocal.get().find(clazz,id)
    }

    override fun <T : Entity> batchSaveUni(entityList: Array<T>): Uni<Boolean> {
        return SessionThreadLocal.get().persistAll(*entityList)
            .map { true }
    }

    override fun <T : Entity> deleteUni(clazz: Class<T>?, id: Serializable?): Uni<Boolean> {
        val session = SessionThreadLocal.get()
        return session.find(clazz, id)
            .chain { it -> if (Objects.nonNull(it)) session.remove(it) else Uni.createFrom().nullItem() }
            .map { true }
    }

    override fun <T> singleQueryUni(clazz: Class<T>?, sql: String, params: Map<String, Any>): Uni<T?> {
        val session = SessionThreadLocal.get()
        val query = session.createQuery(sql, clazz)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        return query.singleResultOrNull
    }

    override fun <T> listQueryUni(clazz: Class<T>?, sql: String, params: Map<String, Any>): Uni<List<T>> {
        val session = SessionThreadLocal.get()
        val query = session.createQuery(sql, clazz)
        params.forEach { (key, value) -> query.setParameter(key, value) }
        return query.resultList
    }

    override fun executeUpdateUni(sql: String, params: Map<String, Any>): Uni<Int> {
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
        }.subscribe().with({ promise.onSuccess(it)},{ promise.fail(it)})
        return promise.future()
    }
}