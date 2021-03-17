package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.*
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class ISVClientRepositoryTest : AbstractTest() {

    private val clientRepository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

    @Test
    fun testPermanentAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify { Assertions.assertNotNull(created) }

                created.permanentAuthCode = randomString()

                val permanent = created.toPermanent().await()
                testContext.verify { Assertions.assertNotNull(permanent) }

                val query = clientRepository.queryPermanentAuthCode(suiteId = created.suiteId,clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()
                testContext.verify { Assertions.assertNotNull(query) }

                val noExists = clientRepository.queryPermanentAuthCode(suiteId = randomString(),clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()
                testContext.verify { Assertions.assertNull(noExists) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryTemporaryAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify { Assertions.assertNotNull(created) }

                val query = clientRepository.queryTemporaryAuthCode(suiteId = created.suiteId,clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()
                testContext.verify { Assertions.assertNotNull(query) }

                val noExists = clientRepository.queryTemporaryAuthCode(suiteId = randomString(),clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()
                testContext.verify { Assertions.assertNull(noExists) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify { Assertions.assertNotNull(created) }

                val query = clientRepository.queryAuthCode(suiteId = created.suiteId,clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()

                testContext.verify { Assertions.assertNotNull(query) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQuerySuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomISVSuiteTicket = randomSuiteTicket()
                val created = clientRepository.save(randomISVSuiteTicket).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id > 0)
                }

                val query = clientRepository.querySuiteTicket(suiteId = created.suiteId,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomSuiteTicket(): ISVSuiteTicket {
        val suiteTicket = ISVSuiteTicket()
        suiteTicket.suiteId = randomString()
        suiteTicket.clientType = ISVClientType.WorkPlusISV
        suiteTicket.suiteTicket = randomString()
        return suiteTicket
    }

    private fun randomISVAuthCode(): ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgId = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }
}