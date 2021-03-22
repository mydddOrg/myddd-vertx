package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForISV
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import java.util.*

class ISVClientTest : AbstractTest() {

    companion object {
        private val randomIdString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
    }

    @Test
    fun testSaveClientAuthExtra(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = ISVClient.createClient(clientName = UUID.randomUUID().toString(),extra = createExtra(),callback = "http://callback.workplus.io")
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                val extra = ISVClientAuthExtraForISV.createInstance(accessToken = randomString(),expireTime = System.currentTimeMillis())
                val updated = created.saveClientAuthExtra(extra).await()
                testContext.verify {
                    Assertions.assertNotNull(updated)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateInstance(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClient = ISVClient.createClient(clientName = UUID.randomUUID().toString(),extra = createExtra(),callback = "http://callback.workplus.io")
                testContext.verify {
                    Assertions.assertNotNull(isvClient)
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testCreateClient(vertx: Vertx,testContext: VertxTestContext){

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                testCreated(vertx,testContext)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }

    }



    @Test
    fun testQueryClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = testCreated(vertx,testContext).await()

                val query = ISVClient.queryClient(created.clientId).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                    Assertions.assertNotNull(query?.oauth2Client)
                }

                val notExists = ISVClient.queryClient(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(notExists)
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = testCreated(vertx,testContext).await()
                created.callback = "http://new.workplus.io"

                val updated = created.updateISVClient().await()

                testContext.verify {
                    Assertions.assertNotNull(updated)
                    Assertions.assertEquals("http://new.workplus.io",updated.callback)
                }

                try {
                    val notExists = ISVClient()
                    notExists.clientId = UUID.randomUUID().toString()
                    notExists.updateISVClient().await()
                }catch (e:Throwable){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testResetClientSecret(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = testCreated(vertx,testContext).await()
                val renewed = created.resetSecret().await()
                testContext.verify {
                    Assertions.assertNotEquals(created.oauth2Client.clientSecret,renewed.oauth2Client.clientSecret)
                }

                try {
                    val notExists = ISVClient()
                    notExists.clientId = UUID.randomUUID().toString()
                    notExists.resetSecret().await()
                }catch (e:Throwable){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun testCreated(vertx: Vertx,testContext: VertxTestContext):Future<ISVClient>{
        return try {
            val isvClient = ISVClient.createClient(clientName = UUID.randomUUID().toString(),extra = createExtra(),callback = "http://callback.workplus.io")

            val created = isvClient.createISVClient().await()
            testContext.verify {
                Assertions.assertNotNull(created)
                Assertions.assertNotNull(created.oauth2Client)
                Assertions.assertTrue(created.getId() > 0)
            }
            Future.succeededFuture(created)
        }catch (e:Exception){
            testContext.failNow(e)
            Future.failedFuture(e)
        }
    }

    @Test
    fun testCreateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClient = ISVClient.createClient(clientName = UUID.randomUUID().toString(),extra = createISVExtra(),callback = "http://callback.workplus.io")

                val created = isvClient.createISVClient().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.oauth2Client)
                    Assertions.assertTrue(created.getId() > 0)
                }

            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateWorkWeiXinClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val workWeiXin = ISVClient.createClient(clientName = randomString(),extra = createWorkWeiXinExtra(),callback = "http://callback.workplus.io")

                val created = workWeiXin.createISVClient().await()

                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    private fun createExtra():ISVClientExtra {
        val extra = ISVClientExtraForWorkPlusApp()
        extra.appKey = randomIdString.randomString()
        extra.appSecret = randomIdString.randomString()
        extra.api = "http://api.workplus.io"
        extra.domainId = "atwork"
        extra.ownerId = randomIdString.randomString()

        return extra
    }

    private fun createISVExtra():ISVClientExtra {
        val extra = ISVClientExtraForWorkPlusISV()
        extra.suiteKey = randomString()
        extra.suiteSecret = randomString()
        extra.token = randomString()
        extra.encryptSecret = randomString()
        extra.isvApi = randomString()
        extra.appId = randomString()
        extra.vendorKey = randomString()
        return extra
    }

    private fun createWorkWeiXinExtra():ISVClientExtra {
        val extra = ISVClientExtraForWorkWeiXin()
        extra.suiteId = randomString()
        extra.suiteSecret = randomString()
        extra.corpId = randomString()
        extra.providerSecret = randomString()
        extra.token = randomString()
        extra.encodingAESKey = randomString()
        return extra
    }
}