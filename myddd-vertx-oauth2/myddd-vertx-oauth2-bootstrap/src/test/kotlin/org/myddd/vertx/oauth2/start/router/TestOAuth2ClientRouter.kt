package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.AbstractWebTest
import org.myddd.vertx.oauth2.start.OAuth2Verticle
import java.util.*
import kotlin.random.Random

@ExtendWith(VertxExtension::class)
class TestOAuth2ClientRouter {

    companion object {


        private val logger by lazy { LoggerFactory.getLogger(AbstractWebTest::class.java) }

        private var port = Random.nextInt(10000,11000)

        private const val host = "127.0.0.1"

        private var deployId:String? = null

        lateinit var webClient: WebClient

        @BeforeAll
        @JvmStatic
        fun startVerticle(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                webClient = WebClient.create(vertx)
                deployId = vertx.deployVerticle(OAuth2Verticle(port = port)).await()
                testContext.completeNow()
            }
        }

        @JvmStatic
        @AfterAll
        fun stopVerticle(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                vertx.undeploy(deployId).await()
                testContext.completeNow()
            }
        }

    }

    @Test
    fun emptyTest(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify { Assertions.assertEquals(1,1) }
        testContext.completeNow()
    }

    private suspend fun createRandomClient(webClient: WebClient,testContext: VertxTestContext): Future<OAuth2ClientDTO> {
        return try {
            val response = webClient.post(port, host,"/v1/oauth2/clients")
                .sendJsonObject(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"name\":\"${UUID.randomUUID()}\"}")).await()
            testContext.verify { Assertions.assertTrue(response.statusCode() == 200) }

            val created = response.bodyAsJson(OAuth2ClientDTO::class.java)
            testContext.verify {
                Assertions.assertNotNull(created)
                Assertions.assertTrue(created.id!! > 0L)
                Assertions.assertNotNull(created.clientSecret)
            }
            Future.succeededFuture(created)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}