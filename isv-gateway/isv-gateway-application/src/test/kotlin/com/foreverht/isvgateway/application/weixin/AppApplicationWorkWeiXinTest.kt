package com.foreverht.isvgateway.application.weixin

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

class AppApplicationWorkWeiXinTest: AbstractWorkWeiXinTest() {

    private val appApplication by lazy { InstanceFactory.getInstance(AppApplication::class.java,WORK_WEI_XIN) }

    @Test
    fun testGetAdminList(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val adminList = appApplication.getAdminList(isvAccessToken = isvAccessToken).await()
                testContext.verify {
                    Assertions.assertTrue(adminList.isNotEmpty())
                }

                try {
                    appApplication.getAdminList(isvAccessToken = randomString()).await()
                    testContext.failNow("不可能到这里")
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
    fun testGetAppDetail(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val appDT0 = appApplication.getAppDetail(isvAccessToken = isvAccessToken).await()
                testContext.verify {
                    Assertions.assertNotNull(appDT0)
                }

                try {
                    appApplication.getAppDetail(isvAccessToken = randomString()).await()
                    testContext.failNow("随机ID不可能找得到")
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