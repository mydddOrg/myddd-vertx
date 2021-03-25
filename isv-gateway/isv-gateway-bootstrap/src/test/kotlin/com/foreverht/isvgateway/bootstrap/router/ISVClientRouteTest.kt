package com.foreverht.isvgateway.bootstrap.router

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.json.AsyncJsonMapper
import java.util.*

class ISVClientRouteTest : AbstractRouteTest(){

    private val logger by lazy { LoggerFactory.getLogger(ISVClientRouteTest::class.java) }

    private val isvClientApplication: ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    companion object {

        const val api = "http://test248.workplus.io/api4/v1"

        const val domainId = "workplus"

        const val clientId = "02018e570da2f42bf598d2f5628183d158e22a72"

        const val clientSecret = "63d3237269214272be13fbab7da791f3"

        const val ownerId = "2975ff5f83a34f458280fd25fbd3a356"

        const val orgId = "aHexITjYkEurKyyxpKMgFh"
    }

    @Test
    fun testApiTokenRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val created = isvClientApplication.createISVClient(realWorkPlusAppClientDTO()).await()

                testContext.verify { Assertions.assertNotNull(created) }


                val requestJson = json {
                    obj(
                        "clientId" to created.clientId,
                        "clientSecret" to created.clientSecret,
                        "domainId" to domainId,
                        "orgCode" to ownerId
                    )
                }

                val response = webClient.post(port,host,"/v1/api/token")
                    .sendJsonObject(requestJson)
                    .await()

                testContext.verify {
                    val body = response.bodyAsJsonObject()
                    Assertions.assertEquals(200,response.statusCode())
                    Assertions.assertNotNull(body.getString("accessToken"))
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testJsonToObject(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val dto = AsyncJsonMapper.mapFrom(vertx,randomAppClient().toString(),ISVClientDTO::class.java).await()
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
                val clients = arrayOf(randomAppClient(),randomISVClient(),randomWorkWeiXinClient())
                clients.forEach {
                    val response = webClient.post(port, host,"/v1/clients")
                        .sendJsonObject(it).await()

                    testContext.verify {
                        logger.debug(response.bodyAsString())
                        Assertions.assertEquals(200,response.statusCode())
                    }
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
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomAppClient()).await()

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
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomAppClient()).await()

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
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                //error
                var errorResponse = webClient.post(port,host,"/v1/clients/token")
                    .sendJson(JsonObject("{\"clientId\":\"${UUID.randomUUID()}\",\"clientSecret\":\"${UUID.randomUUID()}\",\"grantType\":\"client_credentials\"}"))
                    .await()

                testContext.verify { Assertions.assertEquals(400,errorResponse.statusCode()) }


                var response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(randomAppClient()).await()

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

                val requestResponse = webClient.post(port,host,"/v1/clients/token")
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
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
        val response = webClient.post(port, host, "/v1/clients")
            .sendJsonObject(randomAppClient()).await()

        testContext.verify {
            logger.debug(response.bodyAsString())
            Assertions.assertEquals(200, response.statusCode())
        }

        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper.readValue(response.bodyAsString(), ISVClientDTO::class.java)
    }

    private fun randomAppClient(): JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "appKey" to UUID.randomUUID().toString(),
                    "appSecret" to UUID.randomUUID().toString(),
                    "api" to UUID.randomUUID().toString(),
                    "domainId" to UUID.randomUUID().toString(),
                    "ownerId" to UUID.randomUUID().toString(),
                    "clientType" to "WorkPlusApp"
                    )
            )
        }
    }

    private fun randomISVClient():JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "suiteKey" to UUID.randomUUID().toString(),
                    "suiteSecret" to UUID.randomUUID().toString(),
                    "vendorKey" to UUID.randomUUID().toString(),
                    "token" to UUID.randomUUID().toString(),
                    "encryptSecret" to UUID.randomUUID().toString(),
                    "isvApi" to UUID.randomUUID().toString(),
                    "appId" to UUID.randomUUID().toString(),
                    "clientType" to "WorkPlusISV"
                )
            )
        }
    }

    private fun randomWorkWeiXinClient():JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "corpId" to UUID.randomUUID().toString(),
                    "providerSecret" to UUID.randomUUID().toString(),
                    "suiteId" to UUID.randomUUID().toString(),
                    "suiteSecret" to UUID.randomUUID().toString(),
                    "token" to UUID.randomUUID().toString(),
                    "encodingAESKey" to UUID.randomUUID().toString(),
                    "clientType" to "WorkWeiXin"
                )
            )
        }
    }
    private fun realWorkPlusAppClientDTO() : ISVClientDTO {
        val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
            appKey = clientId,
            appSecret = clientSecret,
            domainId = domainId,
            api = api,
            ownerId = ownerId
        )

        return ISVClientDTO(clientName = "WorkPlus Test App",extra = isvClientExtraDTO,callback = api)
    }
}