package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractW6SBossTest
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.domain.ISVClient
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class W6SBossApplicationTest : AbstractW6SBossTest() {

    private val w6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

    companion object {
        private const val ORG_CODE = "2975ff5f83a34f458280fd25fbd3a356"
        private const val DOMAIN_ID = "workplus"
    }

    @BeforeEach
    fun beforeEach(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)
                saveSuiteTicketToLocal(webClient = webClient).await()
                saveTmpAuthCodeToLocal(webClient = webClient).await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(6)
    fun testActiveSuiteForSuiteApplication(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                isvSuiteTicketApplication.activeSuite(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    @Order(5)
    fun testRequestApiAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    w6SBossApplication.requestApiAccessToken(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                }catch (t:Throwable){
                    Assertions.assertNotNull(t)
                }

                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                }


                val isvClientToken = w6SBossApplication.requestApiAccessToken(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClientToken)
                    Assertions.assertNotNull(isvClientToken.extra)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(4)
    fun testActiveSuite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                }

                val success = w6SBossApplication.activeSuite(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify { Assertions.assertTrue(success) }
            }catch (t:Throwable){
                logger.error(t.message)
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(3)
    fun testRequestPermanentCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                    logger.info("【永久激活码】:${permanent.permanentAuthCode}")
                }


                try {
                    w6SBossApplication.requestPermanentCode(clientId = UUID.randomUUID().toString(),domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = UUID.randomUUID().toString()).await()
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
    @Order(2)
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

    @Test
    @Order(1)
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




}