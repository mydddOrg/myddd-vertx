package org.myddd.vertx.oauth2.application

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.domain.OAuth2Client
import java.util.*

class TestOAuth2Application : AbstractTest() {

    private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java)}

    @Test
    fun testQueryValidClientByToken(testContext: VertxTestContext){
        testContext.execute {
            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = UUID.randomUUID().toString()
            val created = client.createClient().await()
            val userDTO = databaseOAuth2Application.requestClientToken(created.clientId,created.clientSecret).await()
            testContext.verify {
                Assertions.assertNotNull(userDTO)
                Assertions.assertFalse(userDTO!!.expired())
            }

            val clientId = databaseOAuth2Application.queryValidClientIdByAccessToken(userDTO!!.tokenDTO!!.accessToken).await()
            testContext.verify {
                Assertions.assertNotNull(clientId)
                Assertions.assertEquals(client.clientId,clientId)
            }

            try {
                databaseOAuth2Application.queryValidClientIdByAccessToken(UUID.randomUUID().toString())
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @Test
    fun testRequestClientToken(testContext: VertxTestContext){
        testContext.execute {
            try {
                databaseOAuth2Application.requestClientToken(UUID.randomUUID().toString(),UUID.randomUUID().toString()).await()
                testContext.failNow("不存在的ClientId不可能验证通过")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = UUID.randomUUID().toString()
            val created = client.createClient().await()

            try {
                databaseOAuth2Application.requestClientToken(created.clientId,UUID.randomUUID().toString()).await()
                testContext.failNow("不正确的密码不可能验证通过")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }


            var userDTO = databaseOAuth2Application.requestClientToken(created.clientId,created.clientSecret).await()
            testContext.verify {
                Assertions.assertNotNull(userDTO)
                Assertions.assertFalse(userDTO!!.expired())
            }

            val disabled = created.disable().await()

            try {
                databaseOAuth2Application.requestClientToken(disabled.clientId,disabled.clientSecret).await()
                testContext.failNow("被禁用的不可能通过验证")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val enabled = disabled.enable().await()

            userDTO = databaseOAuth2Application.requestClientToken(enabled.clientId,enabled.clientSecret).await()
            testContext.verify {
                Assertions.assertNotNull(userDTO)
                Assertions.assertFalse(userDTO!!.expired())
            }
        }
    }

    @Test
    fun testRefreshUserToken(testContext: VertxTestContext){
        testContext.execute {
            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = UUID.randomUUID().toString()
            val created = client.createClient().await()

            val userDTO = databaseOAuth2Application.requestClientToken(created.clientId,created.clientSecret).await()

            try {
                databaseOAuth2Application.refreshUserToken(UUID.randomUUID().toString(),UUID.randomUUID().toString()).await()
                testContext.failNow("不可能刷新TOKEN成功")
            }catch (e:Exception){
                testContext.verify {
                    Assertions.assertNotNull(e)
                }
            }

            try {
                databaseOAuth2Application.refreshUserToken(userDTO!!.clientId,UUID.randomUUID().toString()).await()
                testContext.failNow("不可能刷新TOKEN成功")
            }catch (e:Exception){
                testContext.verify {
                    Assertions.assertNotNull(e)
                }
            }

            testContext.verify {
                Assertions.assertNotNull(userDTO)
            }

            val updated = databaseOAuth2Application.refreshUserToken(userDTO!!.clientId,userDTO.tokenDTO!!.refreshToken).await()
            testContext.verify {
                Assertions.assertNotNull(updated)
            }
        }
    }



    @Test
    fun testRevokeUserToken(testContext: VertxTestContext){
        testContext.execute {
            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = UUID.randomUUID().toString()
            val created = client.createClient().await()


            try{
                databaseOAuth2Application.revokeUserToken(UUID.randomUUID().toString(),UUID.randomUUID().toString()).await()
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val token = databaseOAuth2Application.requestClientToken(created.clientId,created.clientSecret).await()
            testContext.verify {
                Assertions.assertNotNull(token)
                Assertions.assertNotNull(token!!.tokenDTO)
            }
            val success = databaseOAuth2Application.revokeUserToken(created.clientId,token!!.tokenDTO!!.accessToken).await()
            testContext.verify { Assertions.assertTrue(success) }
        }
    }

    @Test
    fun testLoadUserToken(testContext: VertxTestContext){
        testContext.execute {
            try {
                databaseOAuth2Application.loadUserToken(UUID.randomUUID().toString()).await()
                testContext.failNow("没有当前用户，不可能查出相关TOKEN")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = UUID.randomUUID().toString()

            val created = client.createClient().await()

            try {
                databaseOAuth2Application.loadUserToken(created.clientId).await()
                testContext.failNow("当前用户没有请求过TOKEN，不可能查出相关TOKEN")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val userDTO = databaseOAuth2Application.requestClientToken(created.clientId,created.clientSecret).await()
            testContext.verify {
                Assertions.assertNotNull(userDTO)
                Assertions.assertFalse(userDTO!!.expired())
            }

            val queryUserDTO = databaseOAuth2Application.loadUserToken(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(queryUserDTO)
            }

            databaseOAuth2Application.revokeUserToken(created.clientId,userDTO!!.tokenDTO!!.accessToken).await()

            try {
                databaseOAuth2Application.loadUserToken(created.clientId).await()
                testContext.failNow("当前用户没有请求过TOKEN，不可能查出相关TOKEN")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }
        }
    }
}