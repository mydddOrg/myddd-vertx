package org.myddd.vertx.oauth2.domain

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class TestOAuth2Client : AbstractTest() {


    @Test
    fun testCreateInstance(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val oAuth2Client = OAuth2Client.createInstance(UUID.randomUUID().toString())
                testContext.verify { Assertions.assertNotNull(oAuth2Client) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateClientWithoutName(vertx: Vertx,testContext: VertxTestContext){
        val client = OAuth2Client()
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                client.createClient().await()
                testContext.failed()
            }catch (e:Exception){
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testCreateClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val client = createClient()
                val created = client.createClient().await()
                testContext.verify {
                    Assertions.assertTrue(created.getId() > 0)
                    Assertions.assertNotNull(created.clientId)
                    Assertions.assertNotNull(created.clientSecret)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testRenewClientSecret(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val client = createClient()
                val created = client.createClient().await()
                val createdSecret = created.clientSecret
                val updated = created.renewClientSecret().await()
                testContext.verify {
                    Assertions.assertNotNull(updated.clientSecret)
                    Assertions.assertFalse(createdSecret == updated.clientSecret)
                }
                testContext.completeNow()
            }catch (e:Exception){
                e.printStackTrace()
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testDisable(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val client = createClient()
                val created = client.createClient().await()
                testContext.verify {
                    Assertions.assertFalse(created.disabled)
                }
                val disabled = created.disable().await()
                testContext.verify {
                    Assertions.assertNotNull(disabled)
                    Assertions.assertTrue(disabled.disabled)
                }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testEnable(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val client = createClient()
                val created = client.createClient().await()
                testContext.verify {
                    Assertions.assertFalse(created.disabled)
                }

                var disabled = created.disable().await()
                testContext.verify {
                    Assertions.assertNotNull(disabled)
                }

                val enabled = disabled.enable().await()
                testContext.verify {
                    Assertions.assertNotNull(enabled)
                    Assertions.assertFalse(enabled.disabled)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    private fun createClient():OAuth2Client {
        val client = OAuth2Client()
        client.clientId = UUID.randomUUID().toString()
        client.name = "TEST_A"
        client.description = "这是一个测试应用"
        return client
    }
}