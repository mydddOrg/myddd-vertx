package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class AccessTokenApplicationTest : AbstractTest() {

    companion object {

        private val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlusApp") }

        private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

        private val logger = LoggerFactory.getLogger(AccessTokenApplicationTest::class.java)

        const val api = "http://test248.workplus.io/api4/v1"

        const val domainId = "workplus"

        const val clientId = "02018e570da2f42bf598d2f5628183d158e22a72"

        const val clientSecret = "63d3237269214272be13fbab7da791f3"

        const val ownerId = "2975ff5f83a34f458280fd25fbd3a356"

        private lateinit var isvClientId:String

        private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        private fun realISVClient() : ISVClientDTO {
            val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
                clientId = clientId,
                clientSecret = clientSecret,
                domainId = domainId,
                api = api,
                ownerId = ownerId
            )

            return ISVClientDTO(clientName = "WorkPlus Test App",extra = isvClientExtraDTO,callback = api)
        }

        @BeforeAll
        @JvmStatic
        fun anotherBeforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val created = isvClientApplication.createISVClient(realISVClient()).await()

                    testContext.verify {
                        Assertions.assertNotNull(created)
                        Assertions.assertNotNull(created.clientId)
                        Assertions.assertNotNull(created.clientSecret)
                    }

                    created.clientId?.also { isvClientId = it }

                    testContext.completeNow()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }
        }

    }

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