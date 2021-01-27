package org.myddd.vertx.oauth2.domain

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestOAuth2Client : AbstractTest() {

    @Test
    fun testCreateClientWithoutName(testContext: VertxTestContext){
        val client = OAuth2Client()
        GlobalScope.launch {
            try {
                client.createClient().await()
                testContext.failed()
            }catch (e:Exception){
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testCreateClient(testContext: VertxTestContext){
        GlobalScope.launch {
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
    fun testRenewClientSecret(testContext: VertxTestContext){
        GlobalScope.launch {
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

    private fun createClient():OAuth2Client {
        val client = OAuth2Client()
        client.name = "TEST_A"
        client.displayName = "测试应用"
        client.description = "这是一个测试应用"
        return client
    }
}