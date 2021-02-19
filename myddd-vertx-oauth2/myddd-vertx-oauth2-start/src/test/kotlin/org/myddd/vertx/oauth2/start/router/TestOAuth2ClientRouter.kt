package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.AbstractWebTest

class TestOAuth2ClientRouter : AbstractWebTest() {

    @Test
    fun testCreateClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)

                //ok
                val response = webClient.post(port,host,"/v1/oauth2/clients")
                    .sendJsonObject(JsonObject("{\"clientId\":\"AAA\",\"name\":\"AAA\"}")).await()
                testContext.verify { Assertions.assertTrue(response.statusCode() == 200) }

                val created = response.bodyAsJson(OAuth2ClientDTO::class.java)
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.id!! > 0L)
                    Assertions.assertNotNull(created.clientSecret)
                }

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

}