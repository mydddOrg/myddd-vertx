package org.myddd.vertx.oauth2.application

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.DatabaseOAuth2Application
import org.myddd.vertx.oauth2.domain.OAuth2Client
import java.util.*

class TestDatabaseOAuth2Application : AbstractTest() {

    private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(DatabaseOAuth2Application::class.java)}

    @Test
    fun testValidateClientUser(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {
                val client = OAuth2Client()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                val userDTO = databaseOAuth2Application.validateClientUser(created.clientId,created.clientSecret).await()
                testContext.verify {
                    Assertions.assertNotNull(userDTO)
                    Assertions.assertFalse(userDTO!!.expired())
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
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                val userDTO = databaseOAuth2Application.validateClientUser(created.clientId,created.clientSecret).await()

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

                val updated = databaseOAuth2Application.refreshUserToken(userDTO!!.clientId,userDTO!!.tokenDTO!!.refreshToken).await()
                testContext.verify {
                    Assertions.assertNotNull(updated)
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
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()


                try{
                    databaseOAuth2Application.revokeUserToken(UUID.randomUUID().toString()).await()
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                databaseOAuth2Application.validateClientUser(created.clientId,created.clientSecret).await()

                val success = databaseOAuth2Application.revokeUserToken(created.clientId).await()
                testContext.verify { Assertions.assertTrue(success) }

                testContext.completeNow()
            }
        }
    }

    @Test
    fun testLoadUserToken(testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch {

                try {
                    databaseOAuth2Application.loadUserToken(UUID.randomUUID().toString()).await()
                    testContext.failNow("没有当前用户，不可能查出相关TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val client = OAuth2Client()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                try {
                    databaseOAuth2Application.loadUserToken(created.clientId).await()
                    testContext.failNow("当前用户没有请求过TOKEN，不可能查出相关TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                val userDTO = databaseOAuth2Application.validateClientUser(created.clientId,created.clientSecret).await()
                testContext.verify {
                    Assertions.assertNotNull(userDTO)
                    Assertions.assertFalse(userDTO!!.expired())
                }

                var queryUserDTO = databaseOAuth2Application.loadUserToken(created.clientId).await()
                testContext.verify {
                    Assertions.assertNotNull(queryUserDTO)
                }

                databaseOAuth2Application.revokeUserToken(created.clientId).await()

                try {
                    databaseOAuth2Application.loadUserToken(created.clientId).await()
                    testContext.failNow("当前用户没有请求过TOKEN，不可能查出相关TOKEN")
                }catch (e:Exception){
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                testContext.completeNow()



            }
        }
    }
}