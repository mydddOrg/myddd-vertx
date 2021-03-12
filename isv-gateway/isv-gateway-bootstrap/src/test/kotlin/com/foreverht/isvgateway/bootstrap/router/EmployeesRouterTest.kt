package com.foreverht.isvgateway.bootstrap.router

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class EmployeesRouterTest : AbstractISVRouterTest() {

    companion object {
        private const val userId = "ce48a6e05f8e4cb8a8a796684d5c991a"
        private const val query = "136318341"
        private val logger:Logger = LoggerFactory.getLogger(EmployeesRouterTest::class.java)
    }


    @Test
    fun testSearchEmployeeById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var response = webClient.get(port,host,"/v1/organizations/$ownerId/employeesSearch?query=$query&accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                var errorResponse = webClient.get(port,host,"/v1/organizations/$ownerId/employeesSearch?query=$query&accessToken=${UUID.randomUUID()}")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations/employeesSearch?query=$query&accessToken=$accessToken")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                logger.error(t.localizedMessage,t)
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testBatchQueryEmployeeById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var response = webClient.get(port,host,"/v1/organizations/$ownerId/employeesBatch?userIds=$userId&accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                var errorResponse = webClient.get(port,host,"/v1/organizations/$ownerId/employeesBatch?userIds=$userId&accessToken=${UUID.randomUUID()}")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations/employeesBatch?userIds=$userId&accessToken=$accessToken")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                logger.error(t.localizedMessage,t)
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryEmployeeById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var response = webClient.get(port,host,"/v1/organizations/$ownerId/employees/$userId?accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                var errorResponse = webClient.get(port,host,"/v1/organizations/$ownerId/employees/$userId?accessToken=${UUID.randomUUID()}")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations/${UUID.randomUUID()}/employees/$userId?accessToken=$accessToken")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations/employees/$userId?accessToken=$accessToken")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(404,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}