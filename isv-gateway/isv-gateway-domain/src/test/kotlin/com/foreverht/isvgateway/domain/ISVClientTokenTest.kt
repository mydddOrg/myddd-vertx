package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusApp
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ISVClientTokenTest : AbstractTest() {

    @Test
    fun testCreateClientTokenByExtra(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()
                val created = ISVClientToken.saveByExtraToken(isvClientTokenExtra, randomIDString.randomString()).await()
                testContext.verify { Assertions.assertNotNull(created) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSaveClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val clientToken = randomClientToken()
                val created = clientToken.saveClientToken().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id > 0)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExists = ISVClientToken.queryByClientId(clientId = randomIDString.randomString()).await()
                testContext.verify {
                    Assertions.assertNull(notExists)
                }

                val clientToken = randomClientToken()
                val created = clientToken.saveClientToken().await()

                val queryClientToken = ISVClientToken.queryByClientId(clientId = created.clientId).await()
                testContext.verify {
                    Assertions.assertNotNull(queryClientToken)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateInstanceFormJsonObject(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val json = JsonObject("{\"access_token\":\"8b53c657c3be9973da527f43a8165b15fb09f1b12a9de59a0ac0475732336ee4\",\"refresh_token\":\"8b53c657c3be9973da527f43a8165b15fb09f1b12a9de59a0ac0475732336ee4\",\"client_id\":\"02018e570da2f42bf598d2f5628183d158e22a72\",\"expire_time\":1617864984591,\"issued_time\":1615272984591}")
                val extra = ISVClientTokenExtraForWorkPlusApp.createInstanceFormJsonObject(json)
                testContext.verify {
                    Assertions.assertNotNull(extra)
                    Assertions.assertEquals("8b53c657c3be9973da527f43a8165b15fb09f1b12a9de59a0ac0475732336ee4",extra.accessToken)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }

    @Test
    fun testIsTokenValid(vertx: Vertx,testContext: VertxTestContext){
        val isvClientTokenExtra = isvClientTokenExtraForWorkPlusApp()

        Assertions.assertTrue(isvClientTokenExtra.accessTokenValid())

        isvClientTokenExtra.expireTime = System.currentTimeMillis() - 1
        Assertions.assertFalse(isvClientTokenExtra.accessTokenValid())

        testContext.completeNow()

    }

    private fun isvClientTokenExtraForWorkPlusApp(): ISVClientTokenExtraForWorkPlusApp {
        val isvClientTokenExtra = ISVClientTokenExtraForWorkPlusApp()
        isvClientTokenExtra.accessToken = randomIDString.randomString()
        isvClientTokenExtra.clientId = randomIDString.randomString()
        isvClientTokenExtra.expireTime = System.currentTimeMillis() + 1000 * 60
        isvClientTokenExtra.issuedTime = System.currentTimeMillis()
        isvClientTokenExtra.refreshToken = randomIDString.randomString()
        return isvClientTokenExtra
    }

    private fun randomClientToken():ISVClientToken {
        val isvClientToken = ISVClientToken()
        isvClientToken.clientId = randomIDString.randomString()
        isvClientToken.token = randomIDString.randomString()
        isvClientToken.clientType = ISVClientType.WorkPlusApp

        val isvClientTokenExtra = ISVClientTokenExtraForWorkPlusApp()
        isvClientTokenExtra.accessToken = randomIDString.randomString()
        isvClientTokenExtra.clientId = randomIDString.randomString()
        isvClientTokenExtra.expireTime = System.currentTimeMillis()
        isvClientTokenExtra.issuedTime = System.currentTimeMillis()
        isvClientTokenExtra.refreshToken = randomIDString.randomString()

        isvClientToken.extra = isvClientTokenExtra
        return isvClientToken
    }
}