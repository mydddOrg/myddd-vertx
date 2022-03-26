package org.myddd.vertx.repository.hibernate
import org.hibernate.reactive.mutiny.Mutiny.Session

object SessionThreadLocal {

    private val threadLocalSession: ThreadLocal<Session> = ThreadLocal<Session>()


    fun set(session:Session){
        threadLocalSession.set(session)
    }

    fun get():Session{
        val session = threadLocalSession.get()
        requireNotNull(session){
            "共享的Session不存在"
        }
        return session
    }

    fun remote(){
        threadLocalSession.remove()
    }

}