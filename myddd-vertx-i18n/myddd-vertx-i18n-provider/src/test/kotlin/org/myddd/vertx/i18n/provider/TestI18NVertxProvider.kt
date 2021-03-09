package org.myddd.vertx.i18n.provider

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
import org.myddd.vertx.i18n.I18N

@ExtendWith(VertxExtension::class)
class TestI18NVertxProvider {

    companion object {
        var i18n:I18N = I18NVertxProvider(Vertx.vertx())
    }

    @Test
    fun testI18n(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

}