package org.myddd.vertx.oauth2.domain

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import java.util.*

class TestOAuth2Client : AbstractTest() {


    @Test
    fun testCreateInstance(testContext: VertxTestContext){
        testContext.execute {
            val oAuth2Client = OAuth2Client.createInstance(UUID.randomUUID().toString())
            testContext.verify { Assertions.assertNotNull(oAuth2Client) }
        }
    }

    @Test
    fun testCreateClientWithoutName(testContext: VertxTestContext){
        testContext.execute {
            val client = OAuth2Client()
            testContext.assertThrow(IllegalArgumentException::class.java){
                client.createClient().await()
            }
        }
    }

    @Test
    fun testCreateClient(testContext: VertxTestContext){
        testContext.execute {
            val client = createClient()
            val created = client.createClient().await()
            testContext.verify {
                Assertions.assertTrue(created.getId() > 0)
                Assertions.assertNotNull(created.clientId)
                Assertions.assertNotNull(created.clientSecret)
            }
        }
    }

    @Test
    fun testRenewClientSecret(testContext: VertxTestContext){
        testContext.execute {
            val client = createClient()
            val created = client.createClient().await()
            val createdSecret = created.clientSecret
            val updated = created.renewClientSecret().await()
            testContext.verify {
                Assertions.assertNotNull(updated.clientSecret)
                Assertions.assertFalse(createdSecret == updated.clientSecret)
            }
        }
    }

    @Test
    fun testDisable(testContext: VertxTestContext){
        testContext.execute {
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
        }
    }

    @Test
    fun testEnable(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            val client = createClient()
            val created = client.createClient().await()
            testContext.verify {
                Assertions.assertFalse(created.disabled)
            }

            val disabled = created.disable().await()
            testContext.verify {
                Assertions.assertNotNull(disabled)
            }

            val enabled = disabled.enable().await()
            testContext.verify {
                Assertions.assertNotNull(enabled)
                Assertions.assertFalse(enabled.disabled)
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