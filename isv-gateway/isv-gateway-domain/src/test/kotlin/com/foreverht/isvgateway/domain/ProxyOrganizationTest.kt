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

class ProxyOrganizationTest: AbstractTest() {

    @Test
    fun testQueryOrganizations(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val orgList = mutableListOf<ProxyOrganization>()
                for(i in 1..10){
                    orgList.add(randomOrganization(createdAuthCode))
                }

                ProxyOrganization.batchSaveOrganization(createdAuthCode.id,orgList).await()

                val list = ProxyOrganization.queryOrganizations(authCodeId = createdAuthCode.id).await()
                testContext.verify {
                    Assertions.assertEquals(10,list.size)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testBatchSaveOrganization(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createdAuthCode = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(createdAuthCode)
                }

                val orgList = mutableListOf<ProxyOrganization>()
                for(i in 1..10){
                    orgList.add(randomOrganization(createdAuthCode))
                }

                val begin = System.currentTimeMillis()
                ProxyOrganization.batchSaveOrganization(createdAuthCode.id,orgList).await()
                logger.debug("批量事务时间：${System.currentTimeMillis() - begin}")
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }
}