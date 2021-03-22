package com.foreverht.isvgateway.bootstrap.weixin

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

class WorkWeiXinRouteTest: AbstractRouteTest() {


    companion object {
        private const val body = "{\"ToUserName\":\"wx5823bf96d3bd56c7\",\"FromUserName\":\"mycreate\",\"CreateTime\": 1409659813,\"MsgType\":\"text\",\"Content\":\"hello\",\"MsgId\":4561255354251345929,\"AgentID\": 218}"
    }
    @Test
    fun testPostSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.post(port,host,"/v1/callback/weixin?msg_signature=3a7b08bb8e6dbce3c9671d6fdb69d15066227608&timestamp=1403610513&nonce=380320359")
                    .sendJsonObject(JsonObject(body))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}