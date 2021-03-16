package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ISVSuiteForW6STest : AbstractTest() {


    @Test
    fun testUpdateISVSuite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomISVSuite = randomISVSuite()
                val created = randomISVSuite.createISVSuite().await()

                created.isvApi = randomString()

                val updated = created.updateISVSuite().await()
                testContext.verify {
                    Assertions.assertNotNull(updated)
                }

                try {
                    val errorISV = ISVSuiteForW6S()
                    errorISV.suiteKey = randomString()
                    errorISV.updateISVSuite().await()
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
    fun testQueryISVSuite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomISVSuite = randomISVSuite()
                val created = randomISVSuite.createISVSuite().await()

                val query = ISVSuiteForW6S.queryBySuiteKey(suiteKey = created.suiteKey).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                val notExistQuery = ISVSuiteForW6S.queryBySuiteKey(suiteKey = UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(notExistQuery)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateISVSuite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomISVSuite = randomISVSuite()
                val created = randomISVSuite.createISVSuite().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id > 0)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateInstanceFromExtra(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val instance = ISVSuiteForW6S.createInstanceFromClientExtra(randomExtra())
                testContext.verify {
                    Assertions.assertNotNull(instance)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomExtra(): ISVClientExtraForWorkPlusISV {
        val extra = ISVClientExtraForWorkPlusISV()
        extra.suiteKey = randomString()
        extra.suiteSecret = randomString()
        extra.token = randomString()
        extra.isvApi = randomString()
        extra.encryptSecret = randomString()
        return extra
    }

    private fun randomISVSuite():ISVSuiteForW6S {
        val isvSuite = ISVSuiteForW6S()
        isvSuite.suiteKey = randomString()
        isvSuite.suiteSecret = randomString()
        isvSuite.token = randomString()
        isvSuite.isvApi = randomString()
        isvSuite.encryptSecret = randomString()
        return isvSuite
    }
}