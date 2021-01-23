package cc.lingenliu.comment.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.junit5.VertxExtension
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    init {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Mutiny.SessionFactory::class.java).toInstance(
                    Persistence.createEntityManagerFactory("default")
                    .unwrap(Mutiny.SessionFactory::class.java))

                bind(CommentRepository::class.java).to(CommentRepositoryHibernate::class.java)
            }
        })))
    }

}