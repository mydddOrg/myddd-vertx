package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.application.WorkWeiXinApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class WorkWeiXinApplicationTest:AbstractWorkWeiXinTest() {

    private val workWeiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }

    @Test
    fun testSetSessionInfo(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                workWeiXinApplication.setSessionInfo(clientId = isvWorkWeiXinClientId).await()
                workWeiXinApplication.setSessionInfo(clientId = isvWorkWeiXinClientId,productionMode = true).await()

                try {
                    workWeiXinApplication.setSessionInfo(clientId = randomString()).await()
                    testContext.failNow("不可能到这，ID不存在")
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
    fun testRequestPreAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val preAuthCode = workWeiXinApplication.requestPreAuthCode(clientId = isvWorkWeiXinClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(preAuthCode)
                }

                try {
                    workWeiXinApplication.requestPreAuthCode(clientId = randomString()).await()
                    testContext.failNow("不可能到这，ID不存在")
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
    fun testRequestSuiteAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClient = workWeiXinApplication.requestSuiteAccessToken(clientId = isvWorkWeiXinClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClient.clientAuthExtra)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testSuiteTicketExists(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                testContext.verify {
                    Assertions.assertNotNull(isvWorkWeiXinClientId)
                }

                val suiteTicket = isvSuiteTicketApplication.querySuiteTicket(suiteId = "wx2547800152da0539",clientType = "WorkWeiXin").await()
                testContext.verify {
                    Assertions.assertNotNull(suiteTicket)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }
}