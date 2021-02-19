package org.myddd.vertx.oauth2.domain

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.lang.Exception
import java.util.*

class TestOAuth2ClientService : AbstractTest() {

    private val oAuth2ClientService:OAuth2ClientService by lazy {InstanceFactory.getInstance(OAuth2ClientService::class.java)}

    private val tokenRepository by lazy { InstanceFactory.getInstance(OAuth2TokenRepository::class.java) }
    @Test
    fun testQueryClientByClientId(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.clientId = UUID.randomUUID().toString()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()
                var query = oAuth2ClientService.queryClientByClientId(created.clientId).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
                query = oAuth2ClientService.queryClientByClientId(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(query)
                }
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testGenerateClientToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.clientId = UUID.randomUUID().toString()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                val token = oAuth2ClientService.generateClientToken(created).await()
                testContext.verify {
                    Assertions.assertNotNull(token)
                }

                testContext.completeNow()
            }
        }
    }

    @Test
    fun testRefreshUserToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.clientId = UUID.randomUUID().toString()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                try{
                    oAuth2ClientService.refreshUserToken(created,UUID.randomUUID().toString()).await()
                    testContext.failNow("没有申请过token")
                }catch (e:Exception){
                }

                try{
                    oAuth2ClientService.refreshUserToken(created,UUID.randomUUID().toString()).await()
                    testContext.failNow("refresh token不对，应该不能刷新TOKEN 才对")
                }catch (e:Exception){

                }

                val token = oAuth2ClientService.generateClientToken(created).await()

                val refreshToken = oAuth2ClientService.refreshUserToken(created,token.refreshToken).await()
                testContext.verify {
                    Assertions.assertNotNull(refreshToken)
                    Assertions.assertTrue(refreshToken.updated > 0)
                }
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testRevokeUserToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.clientId = UUID.randomUUID().toString()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()
                val token = oAuth2ClientService.generateClientToken(created).await()
                val success = oAuth2ClientService.revokeUserToken(created).await()
                testContext.verify {
                    Assertions.assertTrue(success)
                }

                val exists = tokenRepository.exists(OAuth2Token::class.java,token.id).await()
                testContext.verify {
                    Assertions.assertFalse(exists)
                }

                testContext.completeNow()
            }
        }
    }

    @Test
    fun testQueryUserToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.clientId = UUID.randomUUID().toString()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()
                val token = oAuth2ClientService.generateClientToken(created).await()

                val queryToken = oAuth2ClientService.queryUserToken(client.clientId).await()

                testContext.verify {
                    Assertions.assertNotNull(queryToken)
                    Assertions.assertEquals(token.accessToken,queryToken!!.accessToken)
                }
                testContext.completeNow()
            }
        }
    }

}