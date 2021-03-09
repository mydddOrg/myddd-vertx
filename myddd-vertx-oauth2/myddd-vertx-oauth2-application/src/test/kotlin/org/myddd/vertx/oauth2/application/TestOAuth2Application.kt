package org.myddd.vertx.oauth2.application

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.domain.OAuth2Client
import java.util.*

class TestOAuth2Application : AbstractTest() {

    private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java)}

    @Test
    fun testRequestClientToken(vertx: Vertx, testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {

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

                    val disabled = client.disable().await()

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

                    testContext.completeNow()

                }catch (e:Exception){
                    testContext.failNow(e)
                }

            }
        }
    }

    @Test
    fun testRefreshUserToken(vertx: Vertx,testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch(vertx.dispatcher()) {
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
                testContext.completeNow()
            }
        }
    }



    @Test
    fun testRevokeUserToken(vertx: Vertx,testContext: VertxTestContext){
        executeWithTryCatch(testContext){
            GlobalScope.launch(vertx.dispatcher()) {

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

                testContext.completeNow()
            }
        }
    }

    @Test
    fun testLoadUserToken(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try{
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

                    var queryUserDTO = databaseOAuth2Application.loadUserToken(created.clientId).await()
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

                    testContext.completeNow()

                }catch (e:Exception){
                    testContext.failNow(e)
                }
            }
    }
}