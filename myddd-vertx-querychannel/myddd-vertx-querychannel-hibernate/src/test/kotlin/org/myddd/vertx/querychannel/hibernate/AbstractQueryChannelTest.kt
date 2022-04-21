package org.myddd.vertx.querychannel.hibernate

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractQueryChannelTest {
    companion object {
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(Vertx.vertx())
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))
                }
            }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.completeNow()
        }
    }
}