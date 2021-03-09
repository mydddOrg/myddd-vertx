package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.OrganizationApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class WorkPlusOrganizationApplicationTest : AbstractWorkPlusTest() {

    companion object {
        private val organizationApplication:OrganizationApplication by lazy { InstanceFactory.getInstance(OrganizationApplication::class.java,"WorkPlusApp") }
    }

    @Test
    fun testInstance(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify { Assertions.assertNotNull(organizationApplication) }
        testContext.completeNow()
    }

    @Test
    fun testQueryOrganizationById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val organizationDTO = organizationApplication.queryOrganizationById(orgCode = ownerId,clientId = isvClientId).await()
                testContext.verify { Assertions.assertNotNull(organizationDTO) }

                val subOrganizationDTO = organizationApplication.queryOrganizationById(clientId = isvClientId,orgCode = ownerId,orgId = "aHexITjYkEurKyyxpKMgFh").await()
                testContext.verify { Assertions.assertNotNull(subOrganizationDTO) }

                try {
                    organizationApplication.queryOrganizationById(orgCode = randomIDString.randomString(),clientId = isvClientId).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    organizationApplication.queryOrganizationById(orgCode = ownerId,clientId = isvClientId, orgId = randomIDString.randomString()).await()
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


