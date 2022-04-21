package org.myddd.vertx.junit

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

@ExtendWith(value = [VertxExtension::class])
abstract class AbstractJunitTest {

    companion object {
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    super.configure()
                    bind(Vertx::class.java).toInstance(Vertx.vertx())
                    bind(IDGenerator::class.java).to(SnowflakeDistributeId::class.java)
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
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