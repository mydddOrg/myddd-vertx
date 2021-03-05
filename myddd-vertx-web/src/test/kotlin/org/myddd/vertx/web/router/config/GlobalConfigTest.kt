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
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.web.router.WebGuice

@ExtendWith(VertxExtension::class)
class GlobalConfigTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx:Vertx,testContext: VertxTestContext){
            GlobalScope.launch {
                InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testGlobalConfig(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                GlobalConfig.loadGlobalConfig().await()
                testContext.verify {
                    Assertions.assertNotNull(GlobalConfig.getConfig())
                }

                val enableWhiteIPFilter = GlobalConfig.getConfig()?.getBoolean("ipFilter.whitelist.enable")

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