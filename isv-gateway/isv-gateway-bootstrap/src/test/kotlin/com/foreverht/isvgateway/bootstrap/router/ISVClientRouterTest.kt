package com.foreverht.isvgateway.bootstrap.router

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
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
import java.util.*

class ISVClientRouterTest : AbstractRouteTest(){

    private val logger by lazy { LoggerFactory.getLogger(ISVClientRouterTest::class.java) }

    @Test
    fun testJsonToObject(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val mapper = ObjectMapper().registerModule(KotlinModule())
                val dto = mapper.readValue(randomISVClientDTO().toString(),ISVClientDTO::class.java)
                logger.info(dto)
            }catch (t:Throwable){
                logger.error(t)
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateISVClientRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomISVClientDTO()).await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryISVClientRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val webClient = WebClient.create(vertx)
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomISVClientDTO()).await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val query = webClient.get(port,host,"/v1/clients/${response.bodyAsJsonObject().getString("clientId")}")
                    .send().await()

                testContext.verify {
                    logger.debug(query.bodyAsString())
                    Assertions.assertEquals(200,query.statusCode())
                    Assertions.assertNotNull(query)
                }

                val notExistsQuery = webClient.get(port,host,"/v1/clients/${UUID.randomUUID()}")
                    .send().await()
                testContext.verify {
                    logger.debug(notExistsQuery.bodyAsString())
                    Assertions.assertEquals(400,notExistsQuery.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val webClient = WebClient.create(vertx)
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomISVClientDTO()).await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val mapper = ObjectMapper().registerModule(KotlinModule())
                val created = mapper.readValue(response.bodyAsString(),ISVClientDTO::class.java)

                created.clientName = UUID.randomUUID().toString()

                val updateResponse = webClient.patch(port,host,"/v1/clients/${created.clientId}")
                    .sendJsonObject(JsonObject.mapFrom(created))
                    .await()

                logger.debug(updateResponse.bodyAsString())

                testContext.verify {
                    Assertions.assertEquals(200,updateResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRequestClientToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {

                //error
                val webClient = WebClient.create(vertx)
                var errorResponse = webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${UUID.randomUUID()}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                var response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomISVClientDTO()).await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val mapper = ObjectMapper().registerModule(KotlinModule())
                val created = mapper.readValue(response.bodyAsString(),ISVClientDTO::class.java)


                errorResponse =  webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                response = webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,response.statusCode()) }

                //not right grant_type
                response = webClient.post(port,host,"/v1/clients/token")
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
                var errorResponse = webClient.post(port,host,"/v1/clients/refreshToken")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"refreshToken\":\"${UUID.randomUUID()}\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 参数不正确

                errorResponse = webClient.post(port,host,"/v1/clients/refreshToken")
                    .sendJson(JsonObject("{\"refreshToken\":\"${UUID.randomUUID()}\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                val created = createISVClient(webClient, testContext)

                var requestResponse = webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }

                val token = requestResponse.bodyAsJsonObject()

                val refreshTokenResponse = webClient.post(port,host,"/v1/clients/refreshToken")
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

                val created = createISVClient(webClient, testContext)

                var requestResponse = webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"clientSecret\":\"${created.clientSecret}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(200,requestResponse.statusCode()) }
                val token = requestResponse.bodyAsJsonObject()

                val revokeResponse = webClient.delete(port,host,"/v1/clients/${created.clientId}/token/${token.getString("accessToken")}")
                    .send()
                    .await()

                testContext.verify { Assertions.assertEquals(204,revokeResponse.statusCode()) }


                //error 不正确的clientId或accessToken
                var errorResponse = webClient.delete(port,host,"/v1/clients/${UUID.randomUUID()}/token/${token.getString("accessToken")}")
                    .send()
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                errorResponse = webClient.delete(port,host,"/v1/clients/${created.clientId}/token/${UUID.randomUUID()}")
                    .sendJson(JsonObject("{\"clientId\":\"${created.clientId}\",\"accessToken\":\"${UUID.randomUUID()}\"}"))
                    .await()
                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }

                //error 参数不齐全
                errorResponse = webClient.delete(port,host,"/v1/clients/${created.clientId}/token/")
                    .send()
                    .await()
                testContext.verify { Assertions.assertEquals(404,errorResponse.statusCode()) }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    private suspend fun createISVClient(webClient: WebClient, testContext: VertxTestContext): ISVClientDTO {
        var response = webClient.post(port, host, "/v1/clients")
            .sendJsonObject(randomISVClientDTO()).await()

        testContext.verify {
            logger.debug(response.bodyAsString())
            Assertions.assertEquals(200, response.statusCode())
        }

        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper.readValue(response.bodyAsString(), ISVClientDTO::class.java)
    }

    private fun randomISVClientDTO(): JsonObject {
        val extraForWorkPlusJson = JsonObject()
            .put("clientId", UUID.randomUUID().toString())
            .put("clientSecret", UUID.randomUUID().toString())
            .put("api", UUID.randomUUID().toString())
            .put("domainId", UUID.randomUUID().toString())
            .put("clientType","WorkPlus_App")

        return JsonObject()
            .put("clientName", UUID.randomUUID().toString())
            .put("callback", UUID.randomUUID().toString())
            .put("extra", extraForWorkPlusJson)
    }
}