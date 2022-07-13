package org.myddd.vertx.config

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute

class TestConfig:AbstractBaseTest() {


    @Test
    fun testLoadConfigFromEnvPath(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            System.setProperty("config","/a.config")
            testContext.assertThrow(Exception::class.java){
                Config.loadGlobalConfig(vertx).await()
            }
        }
    }

    @Test
    fun testSystemConfig(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            System.setProperty("TEST","ABC")

            Config.loadGlobalConfig(vertx).await()
            val value = Config.getString("TEST")

            testContext.verify {
                Assertions.assertEquals("ABC",value)
            }
        }
    }

    @Test
    fun testConfig(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
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
        }
    }
}