package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.*
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

class ISVClientTokenTest : AbstractTest() {

    companion object {

        lateinit var createdISVClient:ISVClient


        @BeforeAll
        @JvmStatic
        fun createISVClient(vertx: Vertx,testContext: VertxTestContext){

            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val isvClient = ISVClient.createClient(clientName = UUID.randomUUID().toString(),extra = createISVExtra(),callback = "http://callback.workplus.io")
                    createdISVClient = isvClient.createISVClient().await()
                    testContext.verify { Assertions.assertNotNull(createdISVClient) }
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }

        }

        private fun createISVExtra():ISVClientExtra {
            val extra = ISVClientExtraForWorkPlusISV()
            extra.suiteKey = randomIDString.randomString()
            extra.suiteSecret = randomIDString.randomString()
            extra.token = randomIDString.randomString()
            extra.encryptSecret = randomIDString.randomString()
            extra.isvApi = randomIDString.randomString()
            extra.appId = randomIDString.randomString()
            extra.vendorKey = randomIDString.randomString()
            return extra
        }
    }

    @Test
    fun testCreateInstance(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val instance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomString(),orgCode = randomString())
                testContext.verify {
                    Assertions.assertNotNull(instance)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateWorkWeiXinInstance(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val workWeiXinExtra = isvClientTokenExtraForWorkWeiXin()
                val weiXinInstance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = workWeiXinExtra,domainId = "WorkWeiXin",orgCode = randomString())
                testContext.verify {
                    Assertions.assertNotNull(weiXinInstance)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateISVClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val instance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomString(),orgCode = randomString())

                val created = instance.createClientToken().await()
                testContext.verify { Assertions.assertNotNull(created) }

                try {
                    val errorInstance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomIDString.randomString(128),orgCode = created.orgCode)
                    errorInstance.createClientToken().await()
                    testContext.failNow("不能执行到这，会抛出异常")
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
    fun testQueryClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val instance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomString(),orgCode = randomString())

                val created = instance.createClientToken().await()
                testContext.verify { Assertions.assertNotNull(created) }

                val query = ISVClientToken.queryClientToken(clientId = createdISVClient.clientId,domainId = created.domainId,orgCode = created.orgCode).await()
                testContext.verify { Assertions.assertNotNull(query) }


                val noExists = ISVClientToken.queryClientToken(clientId = randomString(),domainId = created.domainId,orgCode = created.orgCode).await()
                testContext.verify { Assertions.assertNull(noExists) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateISVClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val instance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomString(),orgCode = randomString())

                val created = instance.createClientToken().await()
                testContext.verify { Assertions.assertNotNull(created) }

                val newToken = randomString()
                isvClientTokenExtra.accessToken = newToken

                val updated = instance.updateByExtraToken(isvClientTokenExtra).await()
                testContext.verify {
                    Assertions.assertNotNull(updated)
                    Assertions.assertEquals(newToken,updated.extra.accessToken())
                }

                try {
                    isvClientTokenExtra.accessToken = randomIDString.randomString(200)
                    instance.updateByExtraToken(isvClientTokenExtra).await()
                    testContext.failNow("ERROR：TOKEN长度过长，不可能成功存储到数据训")
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
    fun testQueryByToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val instance = ISVClientToken.createInstanceByExtra(client = createdISVClient,extra = isvClientTokenExtra,domainId = randomString(),orgCode = randomString())
                val created = instance.createClientToken().await()
                testContext.verify { Assertions.assertNotNull(created) }

                val query = ISVClientToken.queryByToken(token = created.token).await()

                testContext.verify { Assertions.assertNotNull(query) }

                val notExists = ISVClientToken.queryByToken(token = randomString()).await()
                testContext.verify { Assertions.assertNull(notExists) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun isvClientTokenExtraForWorkPlusApp(): ISVClientTokenExtraForWorkPlusApp {
        val isvClientTokenExtra = ISVClientTokenExtraForWorkPlusApp()
        isvClientTokenExtra.accessToken = randomIDString.randomString()
        isvClientTokenExtra.clientId = randomIDString.randomString()
        isvClientTokenExtra.expireTime = System.currentTimeMillis() + 1000 * 60
        isvClientTokenExtra.issuedTime = System.currentTimeMillis()
        isvClientTokenExtra.refreshToken = randomIDString.randomString()
        return isvClientTokenExtra
    }

    private fun isvClientTokenExtraForWorkWeiXin():ISVClientTokenExtra {
        return ISVClientTokenExtraForWorkWeiXin.createInstance(randomString(),7200)
    }

}