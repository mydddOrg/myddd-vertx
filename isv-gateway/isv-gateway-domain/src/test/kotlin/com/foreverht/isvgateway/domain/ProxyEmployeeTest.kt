package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class ProxyEmployeeTest:AbstractTest() {

    private val proxyRepository by lazy { InstanceFactory.getInstance(ProxyRepository::class.java) }

    @Test
    fun testQueryEmployeeByAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val employee = randomEmployee(createdAuthCode)
                val organization = proxyRepository.save(randomOrganization(createdAuthCode)).await()
                employee.relations = arrayListOf(ProxyEmpOrgRelation.createInstance(employee = employee,organization = organization))

                val created = employee.createEmployee().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                val list = ProxyEmployee.queryByAuthCode(authCodeId = createdAuthCode.id).await()
                testContext.verify {
                    Assertions.assertTrue(list.isNotEmpty())
                }


            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testBatchSaveEmployee(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val employee = randomEmployee(createdAuthCode)
                val organization = proxyRepository.save(randomOrganization(createdAuthCode)).await()
                employee.relations = arrayListOf(ProxyEmpOrgRelation.createInstance(employee = employee,organization = organization))

                ProxyEmployee.batchSaveEmployeeList(isvAuthCodeId = createdAuthCode.id,employeeList = arrayListOf(employee)).await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateEmployee(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val employee = randomEmployee(createdAuthCode)
                val organization = proxyRepository.save(randomOrganization(createdAuthCode)).await()
                employee.relations = arrayListOf(ProxyEmpOrgRelation.createInstance(employee = employee,organization = organization))

                val created = employee.createEmployee().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                employee.relations = arrayListOf()
                proxyRepository.save(employee).await()

                val query = proxyRepository.get(ProxyEmployee::class.java,employee.id).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }


            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }




}