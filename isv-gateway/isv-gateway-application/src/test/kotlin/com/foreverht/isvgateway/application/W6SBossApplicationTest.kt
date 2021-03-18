package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractW6SBossTest
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.domain.ISVClient
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class W6SBossApplicationTest : AbstractW6SBossTest() {

    private val w6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }

    @Test
    @Disabled("换取ISV永久激活码只能运行一次，不能重复运行")
    fun testRequestPermanentCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,orgId = "2975ff5f83a34f458280fd25fbd3a356").await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                    logger.info("【永久激活码】:${permanent.permanentAuthCode}")
                }


                try {
                    w6SBossApplication.requestPermanentCode(clientId = UUID.randomUUID().toString(),orgId = "2975ff5f83a34f458280fd25fbd3a356").await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    w6SBossApplication.requestPermanentCode(clientId = isvClientId,orgId = UUID.randomUUID().toString()).await()
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
    fun testGenerateToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val w6SBossApplicationImpl = w6SBossApplication as W6SBossApplicationImpl
                val isvClientToken = w6SBossApplicationImpl.generateToken(clientId = isvClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClientToken)
                }

                try {
                    w6SBossApplicationImpl.generateToken(clientId = randomString()).await()
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
    fun testRequestSuiteToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val w6SBossApplicationImpl = w6SBossApplication as W6SBossApplicationImpl
                val isvClient = ISVClient.queryClient(clientId = isvClientId).await()

                testContext.verify { Assertions.assertNotNull(isvClient) }
                val (suiteToken,expired) = w6SBossApplicationImpl.requestSuiteToken(isvClient!!).await()

                testContext.verify {
                    Assertions.assertNotNull(suiteToken)
                    Assertions.assertTrue(expired > 0)
                }

            }catch (t:Throwable){
                t.printStackTrace()
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRequestISVToken(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                testContext.verify {
                    Assertions.assertNotNull(suiteTicket)
                    Assertions.assertNotNull(isvClientId)
                }

                val isvClientToken = w6SBossApplication.requestISVToken(clientId = isvClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClientToken)
                }

                try {
                    w6SBossApplication.requestISVToken(clientId = randomString()).await()
                }catch (t:Throwable){
                    Assertions.assertNotNull(t)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}