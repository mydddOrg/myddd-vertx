package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.AccessTokenApplication
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class AccessTokenApplicationTest : AbstractTest() {

    private val api = "http://test248.workplus.io/api4/v1"

    private val domainid = "workplus"

    private val clientId = "02018e570da2f42bf598d2f5628183d158e22a72"

    private val clientSecret = "63d3237269214272be13fbab7da791f3"

    private val orgId = "2975ff5f83a34f458280fd25fbd3a356"

    companion object {
        private val logger = LoggerFactory.getLogger(AccessTokenApplicationTest::class.java)
    }

    @Test
    fun testInstance(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlus_App"))
        }
        testContext.completeNow()
    }

    @Test
    fun testRequestToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)

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

    private fun requestTokenJsonObject():JsonObject {
        return JsonObject()
            .put("grant_type","client_credentials")
            .put("scope","app")
            .put("domain_id",domainid)
            .put("org_id",orgId)
            .put("client_id",clientId)
            .put("client_secret",clientSecret)
    }
}