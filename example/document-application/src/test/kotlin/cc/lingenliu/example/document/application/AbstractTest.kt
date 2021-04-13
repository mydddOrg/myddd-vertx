package cc.lingenliu.example.document.application

import cc.lingenliu.example.document.api.DocumentApplication
import cc.lingenliu.example.document.domain.DocumentRepository
import cc.lingenliu.example.document.domain.DocumentRepositoryHibernate
import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {

        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

        val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(DocumentRepository::class.java).to(DocumentRepositoryHibernate::class.java)
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())

                    bind(DocumentApplication::class.java).to(DocumentApplicationProvider::class.java)

                }
            })))
            testContext.completeNow()
        }
    }

    fun randomString():String {
        return randomIDString.randomString()
    }

}