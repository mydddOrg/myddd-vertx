package com.foreverht.isvgateway.bootstrap.weixin

import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
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

class WorkWeiXinRouteTest: AbstractRouteTest() {

    companion object {
        private const val ECHO_PARAMS = "msg_signature=8d61584c28e564ccabaf066070cd16dbc5de2b2e&timestamp=1616466608&nonce=1616164906&echostr=VfgGwdAdAvQV3UnJbDg9KljnXBN9d5YaM8DIBdguAeN41kYeMT5aFdzyHMwTux980vOU5FU/hMJ7K+F+18BcKw=="
        private const val XML_CONTENT = "<xml><ToUserName><![CDATA[wx2547800152da0539]]></ToUserName><Encrypt><![CDATA[E+UWkD1sqnWU5FNOQcm/4zQMAeSjy/GxMkmA/f0b+n++xwL5S4A9JlkJAUqfAioVUPaBfKINjY8DehZc0FODeiwtnQKuirGxp5Wo2Cgku1nzI6xU7XpH3mNm5+8tGtNVskQyq8nnHbaccWfGYjhvwYjnNE7xRDUBKB49vOEKSFdlQHrbVupEH9aaOJU779p9J+0uiUw7obXHkIIO/Jr5uNYpw/8nqVYoMYsOfcVJfUuBicA1yzBfG9UdgcQLIvZCY4baMZg4Ey/e//hGFEjlxUzaTKAMzxoBQuCXjvr6BOOQdCfakGii4UfjhNd7qM+t8i6MxhXGdTtrtagK/xd5ekbSPkSW4GyqDe2IoV/SpmEuL+yHSYgcAU/X5o3T0bXx]]></Encrypt><AgentID><![CDATA[]]></AgentID></xml>"
        private const val POST_SUITE_TICKET_PARAMS = "?msg_signature=fe94dd77f4ead72c0090c1477155fd5a49a79501&timestamp=1616475993&nonce=1616584089"

        private lateinit var isvClientId:String

    }


    private fun realWorkWeiXinISVClient() : JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "clientType" to "WorkWeiXin",
                    "corpId" to "wxeb3c9397ae2712a2",
                    "providerSecret" to "zFzZ5bKQ2vasR2hcgJMkvgped3KUNY-NxmHiIhONoy9z9mLWsxdjHQbABuY6c9_8",
                    "suiteId" to "wx2547800152da0539",
                    "suiteSecret" to "Leis38fRtRAA7tkQ2cffjmWhouKOnCH-PSy2KvCgLbg",
                    "token" to "YLzVPx0SW7eCUl",
                    "encodingAESKey" to "5nuHy1Cg6lw5FBIxi5HVchUpEv2qnxwlYxPBTmkVQvp"
                )
            )
        }
    }

    @BeforeEach
    fun prepareWorkWeiXinClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)

                val workWeiXin = realWorkWeiXinISVClient()
                println(workWeiXin)
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(workWeiXin).await()

                testContext.verify {
                    println(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val bodyJson = response.bodyAsJsonObject()

                isvClientId = bodyJson.getString("clientId")
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testEchoHello(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/v1/callback/weixin/$isvClientId?$ECHO_PARAMS")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(400,response.statusCode())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testPushSuiteTicket(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.post(port,host,"/v1/callback/weixin/$isvClientId$POST_SUITE_TICKET_PARAMS")
                    .putHeader("content-type", "text/xml")
                    .sendBuffer(Buffer.buffer(XML_CONTENT))
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

    @Test
    fun testQuerySuite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.post(port,host,"/v1/callback/weixin/$isvClientId$POST_SUITE_TICKET_PARAMS")
                    .putHeader("content-type", "text/xml")
                    .sendBuffer(Buffer.buffer(XML_CONTENT))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }


                val querySuiteTicket = webClient.get(port,host,"/v1/weixin/tickets/wx2547800152da0539")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,querySuiteTicket.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}