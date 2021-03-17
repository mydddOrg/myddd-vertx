package com.foreverht.isvgateway.bootstrap.router

import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ISVSuiteRouteTest : AbstractRouteTest() {

    companion object {
        private const val ISV_SUITE_TICKET = "{\"vendor_key\":\"k2n23vwy0gEKxpS_Bb237h\",\"suite_key\":\"njVwg-pgkeI5nK11iAdduH\",\"event_type\":\"suite_ticket\",\"param\":{\"suite_ticket\":\"G0OJ5bg0kJwI4cu\"}}"
    }

    @Test
    fun testISVSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.post(port,host,"/v1/w6s/isv")
                    .sendJsonObject(JsonObject(ISV_SUITE_TICKET))
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
}