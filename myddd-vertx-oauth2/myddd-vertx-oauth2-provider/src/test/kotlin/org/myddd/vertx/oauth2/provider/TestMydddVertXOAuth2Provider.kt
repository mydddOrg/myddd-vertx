package org.myddd.vertx.oauth2.provider

import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import org.myddd.vertx.oauth2.domain.OAuth2Client
import java.util.*

class TestMydddVertXOAuth2Provider : AbstractTest() {

    private val oAuth2Auth = MydddVertXOAuth2Provider()

    @Test
    fun testAuthenticate(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val createdClient = createClient().createClient().await()
                try {
                    oAuth2Auth.authenticate(JsonObject().put("clientId",UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())).await()
                    testContext.failNow("不正确的ClientId及ClientSecret不可能允许登录")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    oAuth2Auth.authenticate(JsonObject().put("clientId",UUID.randomUUID().toString())
                        .put("clientSecret",createdClient.clientSecret)).await()
                    testContext.failNow("不正确的ClientSecret不可能允许登录")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val user = oAuth2Auth.authenticate(JsonObject().put("clientId",createdClient.clientId)
                    .put("clientSecret",createdClient.clientSecret)).await()
                testContext.verify {
                    Assertions.assertNotNull(user)
                }
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testRefresh(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val createdClient = createClient().createClient().await()
                val user = oAuth2Auth.authenticate(JsonObject().put("clientId",createdClient.clientId)
                    .put("clientSecret",createdClient.clientSecret)).await()

                try {
                    oAuth2Auth.refresh(null)
                    testContext.failNow("空用户不能刷新TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    oAuth2Auth.refresh(OAuth2UserDTO())
                    testContext.failNow("不正确的User不能刷新TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val token = oAuth2Auth.refresh(user).await()
                testContext.verify {
                    Assertions.assertNotNull(token)
                }

                testContext.completeNow()
            }
        }
    }

    @Test
    fun testRevoke(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val createdClient = createClient().createClient().await()
                val user = oAuth2Auth.authenticate(JsonObject().put("clientId",createdClient.clientId)
                    .put("clientSecret",createdClient.clientSecret)).await()
                oAuth2Auth.revoke(user).await()
                testContext.completeNow()
            }
        }
    }

    private fun createClient(): OAuth2Client {
        val client = OAuth2Client()
        client.name = "TEST_A"
        client.displayName = "测试应用"
        client.description = "这是一个测试应用"
        return client
    }
}