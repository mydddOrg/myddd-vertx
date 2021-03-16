package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ISVSuiteTicketTest : AbstractTest() {

    private val logger = LoggerFactory.getLogger(ISVSuiteTicketTest::class.java)

    @Test
    fun testQueryISVSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomSuiteTicket = randomSuiteTicket()
                val created = randomSuiteTicket.updateSuiteTicket().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id > 0)
                }

                val query = ISVSuiteTicket.querySuiteTicket(suiteId = created.suiteId,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateISVSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomSuiteTicket = randomSuiteTicket()
                val created = randomSuiteTicket.updateSuiteTicket().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id > 0)
                }

                created.suiteTicket = randomString()
                val updated = randomSuiteTicket.updateSuiteTicket().await()

                testContext.verify {
                    Assertions.assertNotNull(updated)
                    Assertions.assertEquals(created.suiteTicket,updated.suiteTicket)
                }

                try {
                    ISVSuiteTicket().updateSuiteTicket().await()
                }catch (t:Throwable){
                    logger.debug(t.message)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }

    private fun randomSuiteTicket():ISVSuiteTicket {
        val suiteTicket = ISVSuiteTicket()
        suiteTicket.suiteId = randomString()
        suiteTicket.clientType = ISVClientType.WorkPlusISV
        suiteTicket.suiteTicket = randomString()
        return suiteTicket
    }

}