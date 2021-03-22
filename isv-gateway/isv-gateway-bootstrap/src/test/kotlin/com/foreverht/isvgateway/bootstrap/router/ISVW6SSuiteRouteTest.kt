package com.foreverht.isvgateway.bootstrap.router

import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import io.vertx.core.Future
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ISVW6SSuiteRouteTest : AbstractRouteTest() {

    companion object {
        private const val ISV_SUITE_TICKET = "{\"vendor_key\":\"k2n23vwy0gEKxpS_Bb237h\",\"suite_key\":\"njVwg-pgkeI5nK11iAdduH\",\"event_type\":\"suite_ticket\",\"param\":{\"suite_ticket\":\"%s\"}}"
        private const val ISV_TMP_CODE = "{\"vendor_key\":\"k2n23vwy0gEKxpS_Bb237h\",\"suite_key\":\"njVwg-pgkeI5nK11iAdduH\",\"event_type\":\"tmp_auth_code\",\"param\":{\"domain_id\":\"workplus\",\"tmp_auth_code\":\"%s\",\"org_code\":\"2975ff5f83a34f458280fd25fbd3a356\"}}"

        private lateinit var realTmpCode:String
        private lateinit var realSuiteTicket:String
        private lateinit var clientId:String

        private val logger by lazy { LoggerFactory.getLogger(ISVW6SSuiteRouteTest::class.java) }
    }

    @Test
    fun testISVTmpAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val suiteTicketJSON = String.format(ISV_SUITE_TICKET, realSuiteTicket)
                var response = webClient.post(port,host,"/v1/callback/isv/$clientId")
                    .sendJsonObject(JsonObject(suiteTicketJSON))
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                val authTmpCode = String.format(ISV_TMP_CODE, realTmpCode)
                response = webClient.post(port,host,"/v1/callback/isv/$clientId")
                    .sendJsonObject(JsonObject(authTmpCode))
                    .await()
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
    fun testISVSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val suiteTicketJSON = String.format(ISV_SUITE_TICKET, realSuiteTicket)
                val response = webClient.post(port,host,"/v1/callback/isv/$clientId")
                    .sendJsonObject(JsonObject(suiteTicketJSON))
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                val queryResponse = webClient.get(port,host,"/v1/w6s/tickets/njVwg-pgkeI5nK11iAdduH")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,queryResponse.statusCode())
                    Assertions.assertNotNull(queryResponse.bodyAsJsonObject().getString("suiteTicket"))
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }



    @BeforeEach
    fun beforeEach(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)

                val response = AbstractRouteTest.webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(realW6SISVClient()).await()

                testContext.verify {
                    println(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val bodyJson = response.bodyAsJsonObject()

                clientId = bodyJson.getString("clientId")

                queryRealSuiteTicket(webClient = webClient).await()
                queryRealTmpCode(webClient = webClient).await()

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun realW6SISVClient() : JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "clientType" to "WorkPlusISV",
                    "suiteKey" to "njVwg-pgkeI5nK11iAdduH",
                    "suiteSecret" to "o0jF8HfNXNYE53o3kV22Vcag2oejnM1n",
                    "vendorKey" to "k2n23vwy0gEKxpS_Bb237h",
                    "token" to "KSbiWeOKpLQeyyVuJUT2X6JOM2iqlWAgosk0d0xXIEL",
                    "encryptSecret" to "CoOREEhw6KPCAyfIRLqVFyysEim0dUkWpC5rmDKaLYR",
                    "isvApi" to "http://test248.workplus.io/v1/isv",
                    "appId" to "Pu-xt6AREHB67AznU9ReDd"
                )
            )
        }
    }

    private suspend fun queryRealTmpCode(webClient: WebClient): Future<Unit> {
        return try {
            val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/w6s/authCode/temporary/njVwg-pgkeI5nK11iAdduH/2975ff5f83a34f458280fd25fbd3a356")
                .send().await()
            if(response.statusCode() == 200){
                val body = response.bodyAsJsonObject()
                realTmpCode = body.getString("temporaryAuthCode")
                Future.succeededFuture()
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun queryRealSuiteTicket(webClient: WebClient): Future<Unit> {
        return try {
            val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/w6s/tickets/njVwg-pgkeI5nK11iAdduH")
                .send().await()
            if(response.statusCode() == 200){
                val body = response.bodyAsJsonObject()
                realSuiteTicket = body.getString("suiteTicket")
                Future.succeededFuture()
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}