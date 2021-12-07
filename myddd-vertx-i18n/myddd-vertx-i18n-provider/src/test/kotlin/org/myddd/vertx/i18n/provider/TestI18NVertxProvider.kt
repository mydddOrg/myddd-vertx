package org.myddd.vertx.i18n.provider

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.junit.execute

class TestI18NVertxProvider:AbstractTest() {

    companion object {
        var i18n:I18N = I18NVertxProvider(Vertx.vertx())
    }

    @Test
    fun testI18n(testContext: VertxTestContext){
        testContext.execute {
            val value = i18n.getMessage(key = "special").await()
            testContext.verify {
                Assertions.assertFalse(value.isNullOrEmpty())
            }

            val notExists = i18n.getMessage(key = "NOT_EXISTS").await()
            testContext.verify {
                Assertions.assertEquals("",notExists)
            }

            val valueWithParams = i18n.getMessage(key = "test1",params = arrayOf("aaa")).await()
            testContext.verify {
                Assertions.assertEquals("aaa",valueWithParams)
            }

            val valueWithLocale = i18n.getMessage(key = "special",language = "en").await()
            testContext.verify {
                Assertions.assertEquals("China",valueWithLocale)
            }

            val valueWithLocaleAndParams = i18n.getMessage(key = "test1",params = arrayOf("aaa"),language = "en").await()
            testContext.verify {
                Assertions.assertEquals("en_aaa",valueWithLocaleAndParams)
            }
        }
    }

}