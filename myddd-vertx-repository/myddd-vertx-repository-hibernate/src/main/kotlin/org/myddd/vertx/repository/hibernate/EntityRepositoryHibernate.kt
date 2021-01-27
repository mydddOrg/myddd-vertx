package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.EntityRepository
import java.io.Serializable
import javax.persistence.Persistence


open class EntityRepositoryHibernate : EntityRepository {

    protected val sessionFactory: Mutiny.SessionFactory by lazy { InstanceFactory.getInstance(Mutiny.SessionFactory::class.java) }

    override suspend fun <T : Entity> save(entity: T): Future<T> {
        val future = PromiseImpl<T>()
        exists(entity::class.java,entity.getId()).onSuccess { exists ->
            if(exists) {
                sessionFactory.withTransaction { session, _ ->
                    session.merge(entity).invoke { merge ->
                        future.onSuccess(merge)
                    }
                }.await().indefinitely()

            }else{
                sessionFactory.withTransaction { session, _ ->
                    session.persist(entity).eventually() {
                        future.onSuccess(entity)
                    }
                }.await().indefinitely()
            }
        }


        return future
    }

    override suspend fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?> {
        val future = PromiseImpl<T>()
        sessionFactory.withSession { session ->
            session.find(clazz,id).invoke {
                    findObj -> future.onSuccess(findObj)
            }
        }.await().indefinitely()
        return future
    }

    override suspend fun <T : Entity> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        val future = PromiseImpl<Boolean>()
        sessionFactory.withSession { session ->
            session.find(clazz,id).invoke {
                    findObj -> if(findObj != null) future.onSuccess(true) else future.onSuccess(false)
            }
        }.await().indefinitely()
        return future
    }

    override suspend fun <T : Entity> batchSave(entityList:Array<T>): Future<Boolean> {
        val future = PromiseImpl<Boolean>()
        sessionFactory.withTransaction { session, _ ->
            session.persistAll(*entityList).eventually {
                future.onSuccess(true)
            }
        }.await().indefinitely()
        return future
    }

    override suspend fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        val future = PromiseImpl<Boolean>()
        sessionFactory.withTransaction { session, _ ->
            session.find(clazz,id).chain {
                findObj ->
                if(findObj!= null) {
                    session.remove(findObj).eventually { future.onSuccess(true) }
                }else{
                    future.onSuccess(false)
                    Uni.createFrom().nullItem()
                }
            }
        }.await().indefinitely()
        return future
    }

    override suspend fun <T : Entity> listQuery(clazz: Class<T>?,sql: String,params: Map<String, Any>): Future<List<T>> {
        val future = PromiseImpl<List<T>>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(sql,clazz)
            params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.resultList
        }.subscribe().with{
            list -> future.onSuccess(list)
        }
        return future
    }

    override suspend fun <T : Entity> singleQuery(clazz: Class<T>?, sql: String, params: Map<String, Any>): Future<T?> {
        val future = PromiseImpl<T?>()
        sessionFactory.withSession { session ->
            val query = session.createQuery(sql,clazz)
            params.forEach { (key, value) -> query.setParameter(key,value)  }
            query.singleResultOrNull
        }.subscribe().with {
            item -> future.onSuccess(item)
        }
        return future
    }

}