package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusApp
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ISVClientTokenTest : AbstractTest() {

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
    fun testIsTokenValid(vertx: Vertx,testContext: VertxTestContext){
        val isvClientTokenExtra = ISVClientTokenExtraForWorkPlusApp()
        isvClientTokenExtra.accessToken = randomIDString.randomString()
        isvClientTokenExtra.clientId = randomIDString.randomString()
        isvClientTokenExtra.expireTime = System.currentTimeMillis() + 1000 * 60
        isvClientTokenExtra.issuedTime = System.currentTimeMillis()
        isvClientTokenExtra.refreshToken = randomIDString.randomString()

        Assertions.assertTrue(isvClientTokenExtra.accessTokenValid())

        isvClientTokenExtra.expireTime = System.currentTimeMillis() - 1
        Assertions.assertFalse(isvClientTokenExtra.accessTokenValid())

        testContext.completeNow()

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