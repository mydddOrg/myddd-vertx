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

class TestOAuth2ClientRouter : AbstractWebTest() {

    @Test
    fun testCreateClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                createRandomClient(webClient,testContext)

                //error
                var errorResponse = webClient.post(port,host,"/v1/oauth2/clients")
                    .sendJsonObject(JsonObject("{\"clientId\":\"AAA\",\"name\":\"AAA\"}")).await()
                testContext.verify { errorResponse.statusCode() == 400 }


                var errorBody = errorResponse.bodyAsString()
                println("error,$errorBody")

                errorResponse = webClient.post(port,host,"/v1/oauth2/clients")
                    .sendJsonObject(JsonObject("{\"clientId\":\"BBB\"}")).await()

                testContext.verify { errorResponse.statusCode() == 400 }
                errorBody = errorResponse.bodyAsString()
                println("error,$errorBody")

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }


    @Test
    fun testResetClientSecret(vertx: Vertx,testContext: VertxTestContext){

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createRandomClient(webClient,testContext)

                val resetSecretResponse = webClient.patch(port,host,"/v1/oauth2/clients/${created.clientId}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${created.clientSecret}\"}")).await()

                val resetSecret = resetSecretResponse.bodyAsJsonObject().getString("clientSecret")

                testContext.verify {
                    Assertions.assertEquals(200,resetSecretResponse.statusCode())
                    Assertions.assertNotNull(resetSecret)
                }

                //error 不正确的clientId
                var errorResponse = webClient.patch(port,host,"/v1/oauth2/clients/${UUID.randomUUID()}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${created.clientSecret}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 不正确的client secret
                errorResponse = webClient.patch(port,host,"/v1/oauth2/clients/${created.clientId}/clientSecret")
                    .sendJsonObject(JsonObject("{\"clientSecret\":\"${UUID.randomUUID()}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                testContext.completeNow()

            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @Test
    fun testDisableClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createRandomClient(webClient,testContext)

                val response = webClient.patch(port,host,"/v1/oauth2/clients/${created.clientId}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${created.clientSecret}\"}")).await()

                testContext.verify { Assertions.assertEquals(204,response.statusCode()) }

                //error bad clientId
                var errorResponse = webClient.patch(port,host,"/v1/oauth2/clients/${UUID.randomUUID()}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${created.clientSecret}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                errorResponse = webClient.patch(port,host,"/v1/oauth2/clients/${created.clientId}/disabledStatus")
                    .sendJson(JsonObject("{\"disabled\":true,\"clientSecret\":\"${UUID.randomUUID()}\"}")).await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    private suspend fun createRandomClient(webClient: WebClient,testContext: VertxTestContext):OAuth2ClientDTO {
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