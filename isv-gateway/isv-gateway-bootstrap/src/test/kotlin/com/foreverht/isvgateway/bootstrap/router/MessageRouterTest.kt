package com.foreverht.isvgateway.bootstrap.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MessageRouterTest : AbstractISVRouterTest() {

    @Test
    fun testSendFileMessage(vertx: Vertx,testContext: VertxTestContext){
        val msgJsonObject = json {
            obj(
                "toUserList" to array("A","B"),
                "forAll" to false,
                "body" to obj(
                    "name" to "aaa.mp3",
                    "size" to 1421312,
                    "mediaId" to UUID.randomUUID().toString(),
                    "msgType" to "FILE"
                )
            )
        }
        sendMessage(vertx,testContext,msgJsonObject)
    }

    @Test
    fun testSendVoiceMessage(vertx: Vertx,testContext: VertxTestContext){
        val msgJsonObject = json {
            obj(
                "toUserList" to array("A","B"),
                "forAll" to false,
                "body" to obj(
                    "duration" to 12,
                    "mediaId" to UUID.randomUUID().toString(),
                    "msgType" to "VOICE"
                )
            )
        }
        sendMessage(vertx,testContext,msgJsonObject)
    }

    @Test
    fun testSendImageMessage(vertx: Vertx,testContext: VertxTestContext){
        val msgJsonObject = json {
            obj(
                "toUserList" to array("A","B"),
                "forAll" to false,
                "body" to obj(
                    "msgType" to "IMAGE",
                    "mediaId" to UUID.randomUUID().toString(),
                    "content" to UUID.randomUUID().toString(),
                    "height" to 100,
                    "width" to 1231.2
                )
            )
        }
        sendMessage(vertx,testContext,msgJsonObject)
    }

    @Test
    fun testSendTextMessage(vertx: Vertx,testContext: VertxTestContext){
        val msgJsonObject = json {
            obj(
                "toUserList" to array("A","B"),
                "forAll" to false,
                "body" to obj(
                    "content" to "2123",
                    "msgType" to "TEXT"
                )
            )
        }
        sendMessage(vertx,testContext,msgJsonObject)
    }

    private fun sendMessage(vertx: Vertx,testContext: VertxTestContext,msgJsonObject: JsonObject){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val response = webClient.post(port,host,"/v1/messages?accessToken=$accessToken")
                    .sendJsonObject(msgJsonObject)
                    .await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(204,response.statusCode())
                }

                var errorResponse = webClient.post(port,host,"/v1/messages?accessToken=${UUID.randomUUID()}")
                    .sendJsonObject(msgJsonObject)
                    .await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }
            }catch (t:Throwable){
                t.printStackTrace()
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}