package org.myddd.vertx.config

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestConfig {


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
    fun testSystemConfig(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                System.setProperty("TEST","ABC")

                Config.loadGlobalConfig(vertx).await()
                val value = Config.getString("TEST")

                testContext.verify {
                    Assertions.assertEquals("ABC",value)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
    @Test
    fun testConfig(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                System.setProperty("config","META-INF/config.properties")

                Config.loadGlobalConfig(vertx).await()

                val enableWhiteIPFilter = Config.getBoolean("ipFilter.whitelist.enable")

                testContext.verify {
                    Assertions.assertEquals(false,enableWhiteIPFilter)
                }

                val withDefaultBoolean = Config.getBoolean("notExists.boolean",true)
                testContext.verify {
                    Assertions.assertTrue(withDefaultBoolean)
                }

                val intValue = Config.getInteger("integer.value")
                testContext.verify {
                    Assertions.assertTrue(intValue > 0)
                }

                val withDefaultIntValue = Config.getInteger("integer.notExists",100)
                testContext.verify {
                    Assertions.assertEquals(100,withDefaultIntValue)
                }

                val longValue = Config.getLong("long.value");
                testContext.verify {
                    Assertions.assertTrue(longValue > 0)
                }

                val withDefaultLongValue = Config.getLong("long.notExists",200L)
                testContext.verify {
                    Assertions.assertEquals(withDefaultLongValue,200L)
                }
                testContext.completeNow()

            }catch (e:Exception){
                e.printStackTrace()
                testContext.failNow(e)
            }
        }
    }
}