package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.EmployeeApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class EmployeeApplicationWorkPlusTest : AbstractWorkPlusTest() {


    companion object {
        private const val userId = "ce48a6e05f8e4cb8a8a796684d5c991a"
        private const val query = "136318341"
        private val employeeApplication:EmployeeApplication by lazy { InstanceFactory.getInstance(EmployeeApplication::class.java,"WorkPlusApp") }
    }


    @Test
    fun testQueryEmployeeById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val employee = employeeApplication.queryEmployeeById(clientId = isvClientId, orgCode = ownerId , userId = userId).await()
                testContext.verify {
                    Assertions.assertNotNull(employee)
                    Assertions.assertEquals(userId,employee.userId)
                }

                try {
                    employeeApplication.queryEmployeeById(clientId = randomIDString.randomString(), orgCode = ownerId , userId = userId)
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
    fun testBatchQueryEmployees(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val employeeList = employeeApplication.batchQueryEmployeeByIds(clientId = isvClientId,orgCode = ownerId,
                    listOf(userId)).await()
                testContext.verify {
                    Assertions.assertNotNull(employeeList)
                    Assertions.assertEquals(1,employeeList.size)
                }

                try {
                    employeeApplication.batchQueryEmployeeByIds(clientId = isvClientId,orgCode = ownerId,
                        listOf()).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    employeeApplication.batchQueryEmployeeByIds(clientId = isvClientId,orgCode = ownerId,
                        listOf(randomIDString.randomString())).await()
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
    fun testSearchEmployee(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val searchResult = employeeApplication.searchEmployees(clientId = isvClientId,orgCode = ownerId, query = query).await()
                testContext.verify {
                    Assertions.assertNotNull(searchResult)
                    Assertions.assertTrue(searchResult.isNotEmpty())
                }

                try {
                    employeeApplication.searchEmployees(clientId = isvClientId,orgCode = ownerId, query = "").await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    employeeApplication.searchEmployees(clientId = randomIDString.randomString(),orgCode = ownerId, query = "").await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    employeeApplication.searchEmployees(clientId = isvClientId,orgCode = randomIDString.randomString(), query = "").await()
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