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
    fun testCreateEmployee(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val employee = randomEmployee(createdAuthCode).createEmployee().await()
                testContext.verify { Assertions.assertNotNull(employee) }

                val organization = proxyRepository.save(randomOrganization(createdAuthCode)).await()
                testContext.verify { Assertions.assertNotNull(organization) }

                val relation = ProxyEmpOrgRelation.createInstance(employee = employee,organization = organization)

                val updated = proxyRepository.save(relation).await()
                testContext.verify { Assertions.assertNotNull(updated) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }




}