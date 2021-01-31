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

                try {
                    oAuth2Auth.authenticate(JsonObject()).await()
                    testContext.failNow("没有clientId 或 clientSecret不允许登录")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val createdClient = createClient().createClient().await()
                try {
                    oAuth2Auth.authenticate(JsonObject().put(MydddVertXOAuth2Provider.CLIENT_ID,UUID.randomUUID().toString())
                        .put(MydddVertXOAuth2Provider.CLIENT_SECRET,UUID.randomUUID().toString())).await()
                    testContext.failNow("不正确的ClientId及ClientSecret不可能允许登录")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    oAuth2Auth.authenticate(JsonObject().put(MydddVertXOAuth2Provider.CLIENT_ID,UUID.randomUUID().toString())
                        .put(MydddVertXOAuth2Provider.CLIENT_SECRET,createdClient.clientSecret)).await()
                    testContext.failNow("不正确的ClientSecret不可能允许登录")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val user = oAuth2Auth.authenticate(JsonObject().put(MydddVertXOAuth2Provider.CLIENT_ID,createdClient.clientId)
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


                try {
                    oAuth2Auth.refresh(null).await()
                    testContext.failNow("空用户不能刷新TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    oAuth2Auth.refresh(OAuth2UserDTO()).await()
                    testContext.failNow("不正确的User不能刷新TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val createdClient = createClient().createClient().await()
                val user = oAuth2Auth.authenticate(JsonObject().put("clientId",createdClient.clientId)
                    .put("clientSecret",createdClient.clientSecret)).await()

                val token = oAuth2Auth.refresh(user).await()
                testContext.verify {
                    Assertions.assertNotNull(token)
                }

                try {
                    val oauthUser = user as OAuth2UserDTO
                    oauthUser.tokenDTO?.refreshToken = UUID.randomUUID().toString()
                    oAuth2Auth.refresh(oauthUser).await()
                    testContext.failNow("不正确的refreshToken不能刷新Token")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
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

                try {
                    oAuth2Auth.revoke(null).await()
                    testContext.failNow("没有client id不能为空")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    val oauthUser = OAuth2UserDTO()
                    oauthUser.clientId = UUID.randomUUID().toString()
                    oAuth2Auth.revoke(oauthUser).await()
                    testContext.failNow("不正确的client Id不能revoke")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

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