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
open class TestOAuth2TokenRouter {

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
                try {
                    webClient = WebClient.create(vertx)
                    deployId = vertx.deployVerticle(OAuth2Verticle(port = port)).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }

                testContext.completeNow()
            }
        }

        @JvmStatic
        @AfterAll
        fun stopVerticle(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    vertx.undeploy(deployId).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

    }

    @Test
    fun testRequestClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                //error
                var errorResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${UUID.randomUUID()}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                val created = createRandomClient(webClient,testContext).await()
                var response = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,response.statusCode()) }


                errorResponse =  webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //disable client
                val disableResponse = webClient.patch(port,host,"/v1/oauth2/clients/${created.clientId}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${created.clientSecret}\"}")).await()

                testContext.verify { Assertions.assertEquals(204,disableResponse.statusCode()) }


                response = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,response.statusCode()) }

                //not right grant_type
                response = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"aaaa\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,response.statusCode()) }

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }
    }
    @Test
    fun testRefreshClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                //error 不正确的值
                var errorResponse = webClient.post(port,host,"/v1/oauth2/refreshToken")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"refreshToken\":\"${UUID.randomUUID()}\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 参数不正确

                errorResponse = webClient.post(port,host,"/v1/oauth2/refreshToken")
                    .sendJson(JsonObject("{\"refreshToken\":\"${UUID.randomUUID()}\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                val created = createRandomClient(webClient,testContext).await()
                val requestResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }

                val token = requestResponse.bodyAsJsonObject()

                val refreshTokenResponse = webClient.post(port,host,"/v1/oauth2/refreshToken")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"refreshToken\":\"${token.getString("refreshToken")}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(200,refreshTokenResponse.statusCode()) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRevokeToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createRandomClient(webClient,testContext).await()
                val requestResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }
                val token = requestResponse.bodyAsJsonObject()

                val revokeResponse = webClient.delete(port,host,"/v1/oauth2/clients/${created.clientId}/token/${token.getString("accessToken")}")
                    .send()
                    .await()

                testContext.verify { Assertions.assertEquals(204,revokeResponse.statusCode()) }


                //error 不正确的clientId或accessToken

                var errorResponse = webClient.delete(port,host,"/v1/oauth2/clients/${UUID.randomUUID()}/token/${token.getString("accessToken")}")
                    .send()
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                errorResponse = webClient.delete(port,host,"/v1/oauth2/clients/${created.clientId}/token/${UUID.randomUUID()}")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"accessToken\":\"${UUID.randomUUID()}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 参数不齐全
                errorResponse = webClient.delete(port,host,"/v1/oauth2/clients/${created.clientId}/token/")
                    .send()
                    .await()
                testContext.verify { Assertions.assertEquals(404,errorResponse.statusCode()) }

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }
    }


    @Test
    fun testCreateClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                createRandomClient(webClient,testContext)

                //error
                var errorResponse = webClient.post(
                    port,
                    host,"/v1/oauth2/clients")
                    .sendJsonObject(JsonObject("{\"clientId\":\"AAA\",\"name\":\"AAA\"}")).await()
                testContext.verify { errorResponse.statusCode() == 400 }


                var errorBody = errorResponse.bodyAsString()
                println("error,$errorBody")

                errorResponse = webClient.post(
                    port,
                    host,"/v1/oauth2/clients")
                    .sendJsonObject(JsonObject("{\"clientId\":\"BBB\"}")).await()

                testContext.verify { errorResponse.statusCode() == 400 }
                errorBody = errorResponse.bodyAsString()
                println("error,$errorBody")

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }
    }


    @Test
    fun testResetClientSecret(vertx: Vertx,testContext: VertxTestContext){

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createRandomClient(webClient,testContext).await()

                val resetSecretResponse = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${created.clientId}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${created.clientSecret}\"}")).await()

                val resetSecret = resetSecretResponse.bodyAsJsonObject().getString("clientSecret")

                testContext.verify {
                    Assertions.assertEquals(200,resetSecretResponse.statusCode())
                    Assertions.assertNotNull(resetSecret)
                }

                //error 不正确的clientId
                var errorResponse = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${UUID.randomUUID()}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${created.clientSecret}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 不正确的client secret
                errorResponse = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${created.clientId}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${UUID.randomUUID()}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                testContext.completeNow()

            }catch (t:Throwable){
                testContext.failNow(t)
            }

        }
    }

    @Test
    fun testDisableClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createRandomClient(webClient,testContext).await()

                val response = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${created.clientId}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${created.clientSecret}\"}")).await()

                testContext.verify { Assertions.assertEquals(204,response.statusCode()) }

                //error bad clientId
                var errorResponse = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${UUID.randomUUID()}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${created.clientSecret}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                errorResponse = webClient.patch(
                    port,
                    host,"/v1/oauth2/clients/${created.clientId}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${UUID.randomUUID()}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }
    }


    private suspend fun createRandomClient(webClient: WebClient,testContext: VertxTestContext): Future<OAuth2ClientDTO> {
        return try {
            val response = webClient.post(port,host,"/v1/oauth2/clients")
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