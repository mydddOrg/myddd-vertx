package org.myddd.vertx.repository.hibernate

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.repository.api.EntityRepository
import java.io.Serializable
import javax.persistence.Persistence

open class EntityRepositoryHibernate : EntityRepository {

    protected val sessionFactory: Mutiny.SessionFactory by lazy { Persistence.createEntityManagerFactory("default")
        .unwrap(Mutiny.SessionFactory::class.java) }

    override fun <T : Entity?> save(entity: T): Future<T> {
        val future = PromiseImpl<T>()
        sessionFactory.withTransaction { session, _ ->
            session.persist(entity).eventually {
                future.onSuccess(entity)
            }
        }.await().indefinitely()
        return future
    }

    override fun <T : Entity?> get(clazz: Class<T>?, id: Serializable?): Future<T?> {
        val future = PromiseImpl<T>()
        sessionFactory.withSession { session ->
            session.find(clazz,id).invoke {
                    user -> future.onSuccess(user)
            }
        }.await().indefinitely()
        return future
    }

    override fun <T : Entity?> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean> {
        val future = PromiseImpl<Boolean>()
        sessionFactory.withSession { session ->
            session.find(clazz,id).invoke {
                    user -> if(user != null) future.onSuccess(true) else future.onSuccess(false)
            }
        }.await().indefinitely()
        return future
    }

}