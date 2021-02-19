package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.AbstractWebTest
import java.util.*

class TestOAuth2TokenRouter : AbstractWebTest() {

    @Test
    fun testRequestClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {

                //error
                val webClient = WebClient.create(vertx)
                var errorResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${UUID.randomUUID()}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                val created = createRandomClient(webClient,testContext)
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
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }
    @Test
    fun testRefreshClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)

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


                val created = createRandomClient(webClient,testContext)
                var requestResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }

                val token = requestResponse.bodyAsJsonObject()

                val refreshTokenResponse = webClient.post(port,host,"/v1/oauth2/refreshToken")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"refreshToken\":\"${token.getString("refreshToken")}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(200,refreshTokenResponse.statusCode()) }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testRevokeToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {

                val webClient = WebClient.create(vertx)
                val created = createRandomClient(webClient,testContext)
                var requestResponse = webClient.post(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }
                val token = requestResponse.bodyAsJsonObject()

                val revokeResponse = webClient.delete(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"accessToken\":\"${token.getString("accessToken")}\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(204,revokeResponse.statusCode()) }


                //error 不正确的clientId或accessToken

                var errorResponse = webClient.delete(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"accessToken\":\"${token.getString("accessToken")}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                errorResponse = webClient.delete(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"accessToken\":\"${UUID.randomUUID()}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 参数不齐全
                errorResponse = webClient.delete(port,host,"/v1/oauth2/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    private suspend fun createRandomClient(webClient: WebClient,testContext: VertxTestContext): OAuth2ClientDTO {
        val response = webClient.post(port,host,"/v1/oauth2/clients")
            .sendJsonObject(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"name\":\"${UUID.randomUUID()}\"}")).await()
        testContext.verify { Assertions.assertTrue(response.statusCode() == 200) }

        val created = response.bodyAsJson(OAuth2ClientDTO::class.java)
        testContext.verify {
            Assertions.assertNotNull(created)
            Assertions.assertTrue(created.id!! > 0L)
            Assertions.assertNotNull(created.clientSecret)
        }

        return created
    }
}