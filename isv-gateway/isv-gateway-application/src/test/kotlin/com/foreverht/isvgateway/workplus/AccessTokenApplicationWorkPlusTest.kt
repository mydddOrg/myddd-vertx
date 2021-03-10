package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.AccessTokenApplication
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class AccessTokenApplicationWorkPlusTest : AbstractWorkPlusTest() {

    @Test
    fun testInstance(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlusApp"))
        }
        testContext.completeNow()
    }

    @Test
    fun testRequestToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val tokenResponse = webClient.postAbs("$api/token")
                    .sendJsonObject(requestTokenJsonObject())
                    .await()


                testContext.verify {
                    logger.debug(tokenResponse.bodyAsString())
                    Assertions.assertEquals(200,tokenResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testRequestAccessTokenByClientId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    accessTokenApplication.requestRequestAccessToken(randomIDString.randomString()).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val accessToken = accessTokenApplication.requestRequestAccessToken(isvClientId).await()
                testContext.verify { Assertions.assertNotNull(accessToken) }

                val queryAccessTokenAgain = accessTokenApplication.requestRequestAccessToken(isvClientId).await()
                testContext.verify { Assertions.assertNotNull(queryAccessTokenAgain) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun requestTokenJsonObject():JsonObject {
        return JsonObject()
            .put("grant_type","client_credentials")
            .put("scope","app")
            .put("domain_id", domainId)
            .put("org_id",ownerId)
            .put("client_id",clientId)
            .put("client_secret",clientSecret)
    }


}