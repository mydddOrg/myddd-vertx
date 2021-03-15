package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.AppApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class AppApplicationWorkPlusTest: AbstractWorkPlusTest()  {

    private val appApplication by lazy { InstanceFactory.getInstance(AppApplication::class.java,"WorkPlusApp") }

    @Test
    fun testGetAdmins(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val admins = appApplication.getAdminList(clientId = isvClientId).await()
                testContext.verify {
                    Assertions.assertTrue(admins.isNotEmpty())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGetAppDetail(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val appDTO = appApplication.getAppDetail(clientId = isvClientId).await()
                testContext.verify {
                    logger.debug(appDTO)
                    Assertions.assertNotNull(appDTO)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}