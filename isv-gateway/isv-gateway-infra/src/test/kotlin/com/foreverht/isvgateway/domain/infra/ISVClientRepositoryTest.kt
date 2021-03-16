package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteTicket
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
}