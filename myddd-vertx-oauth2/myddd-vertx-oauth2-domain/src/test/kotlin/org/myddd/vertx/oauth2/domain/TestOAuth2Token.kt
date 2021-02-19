package org.myddd.vertx.oauth2.domain

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class TestOAuth2Token : AbstractTest() {

    @Test
    fun testCreateTokenFromClient(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = createClient()
                client.clientId = UUID.randomUUID().toString()
                val createdClient = client.createClient().await()
                val token = OAuth2Token.createTokenFromClient(createdClient).await()
                testContext.verify {
                    Assertions.assertNotNull(token)
                    Assertions.assertEquals(token.clientId,createdClient.clientId)
                }
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testRefreshToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = createClient()
                client.clientId = UUID.randomUUID().toString()

                val createdClient = client.createClient().await()
                val token = OAuth2Token.createTokenFromClient(createdClient).await()
                testContext.verify {
                    Assertions.assertNotNull(token)
                    Assertions.assertEquals(token.clientId,createdClient.clientId)
                }

                val updated = token.refreshToken().await()
                testContext.verify {
                    Assertions.assertNotNull(updated)
                }
                testContext.completeNow()
            }
        }
    }

    private fun createClient():OAuth2Client {
        val client = OAuth2Client()
        client.name = UUID.randomUUID().toString()
        client.displayName = "测试应用"
        client.description = "这是一个测试应用"
        return client
    }
}