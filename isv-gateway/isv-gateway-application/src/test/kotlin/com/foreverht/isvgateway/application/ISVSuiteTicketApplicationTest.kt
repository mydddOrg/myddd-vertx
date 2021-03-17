package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
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
import java.util.*

class ISVSuiteTicketApplicationTest : AbstractTest() {

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

    @Test
    fun testQuerySuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvSuiteTicketDTO = ISVSuiteTicketDTO(
                    suiteId = UUID.randomUUID().toString(),
                    clientType = ISVClientType.WorkPlusISV.toString(),
                    suiteTicket = UUID.randomUUID().toString()
                )

                val boolean = isvSuiteTicketApplication.saveSuiteTicket(isvSuiteTicketDTO).await()
                testContext.verify {
                    Assertions.assertTrue(boolean)
                }


                val query = isvSuiteTicketApplication.querySuiteTicket(suiteId = isvSuiteTicketDTO.suiteId,clientType = isvSuiteTicketDTO.clientType).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                try {
                    isvSuiteTicketApplication.querySuiteTicket(suiteId = UUID.randomUUID().toString(),clientType = isvSuiteTicketDTO.clientType).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    isvSuiteTicketApplication.querySuiteTicket(suiteId = isvSuiteTicketDTO.suiteId,clientType = UUID.randomUUID().toString())
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
    fun testSaveISVSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvSuiteTicketDTO = ISVSuiteTicketDTO(
                    suiteId = UUID.randomUUID().toString(),
                    clientType = ISVClientType.WorkPlusISV.toString(),
                    suiteTicket = UUID.randomUUID().toString()
                )

                val boolean = isvSuiteTicketApplication.saveSuiteTicket(isvSuiteTicketDTO).await()
                testContext.verify {
                    Assertions.assertTrue(boolean)
                }

                val query = ISVSuiteTicket.querySuiteTicket(suiteId = isvSuiteTicketDTO.suiteId,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
                try {
                    val errorISVSuiteTicketDTO = ISVSuiteTicketDTO(
                        suiteId = randomIDString.randomString(128),
                        clientType = ISVClientType.WorkPlusISV.toString(),
                        suiteTicket = UUID.randomUUID().toString()
                    )
                    isvSuiteTicketApplication.saveSuiteTicket(errorISVSuiteTicketDTO).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}