package org.myddd.vertx.web.router.config

import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.web.router.WebGuice

@ExtendWith(VertxExtension::class)
class GlobalConfigTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx:Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testLoadConfigFromEnvPath(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                System.setProperty("config","/a.config")
                try {
                    Config.loadGlobalConfig(vertx).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGlobalConfig(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                System.setProperty("config","META-INF/config.properties")

                Config.loadGlobalConfig(vertx).await()

                val enableWhiteIPFilter = Config.getBoolean("ipFilter.whitelist.enable")

                testContext.verify {
                    Assertions.assertEquals(false,enableWhiteIPFilter)
                }
                testContext.completeNow()

            }catch (e:Exception){
                e.printStackTrace()
                testContext.failNow(e)
            }
        }
    }
}