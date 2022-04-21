package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.AbstractWebTest
import java.util.*

class TestOAuth2TokenRouter:AbstractWebTest() {

    @Test
    fun testRequestClientToken(testContext: VertxTestContext){
        testContext.execute {
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
        }
    }
    @Test
    fun testRefreshClientToken(testContext: VertxTestContext){
        testContext.execute {
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
        }
    }

    @Test
    fun testRevokeToken(testContext: VertxTestContext){
        testContext.execute {
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
        }
    }


    @Test
    fun testCreateClient(testContext: VertxTestContext){
        testContext.execute {
            val created = createRandomClient(webClient,testContext).await()

            //error
            var errorResponse = webClient.post(
                port,
                host,"/v1/oauth2/clients")
                .sendJsonObject(JsonObject("{\"clientId\":\"${created.clientId}\",\"name\":\"${created.name}\"}")).await()
            testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


            var errorBody = errorResponse.bodyAsString()

            errorResponse = webClient.post(
                port,
                host,"/v1/oauth2/clients")
                .sendJsonObject(JsonObject("{\"clientId\":\"BBB\"}")).await()

            testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }
        }
    }


    @Test
    fun testResetClientSecret(testContext: VertxTestContext){

        testContext.execute {
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
        }
    }

    @Test
    fun testDisableClient(testContext: VertxTestContext){
        testContext.execute {
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