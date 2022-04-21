package org.myddd.vertx.cache

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object{
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(Vertx.vertx())
                    bind(Cache::class.java).annotatedWith(Names.named("Cache")).toInstance(ShareDataCache<Entity>(name = "Cache"))
                    bind(Cache::class.java).annotatedWith(Names.named("AsyncCache")).toInstance(ShareDataCache<Entity>(name = "AsyncCache",localCache = false))

                    bind(Cache::class.java).annotatedWith(Names.named("AnotherCache")).toInstance(ShareDataCache<Entity>(name = "AnotherCache"))
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