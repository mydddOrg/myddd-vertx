package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session

class MutinySessionObject: org.myddd.vertx.repository.api.SessionObject {


    private lateinit var session:Session

    companion object {
        fun wrapper(session: Session): org.myddd.vertx.repository.api.SessionObject {
            val instance = MutinySessionObject()
            instance.session = session
            return instance
        }
    }

    override fun getSession(): Session {
        return session
    }

    fun <T> execute(block: (session:Session)-> Uni<T>): Uni<T> {
        return block(this.getSession())
    }
}