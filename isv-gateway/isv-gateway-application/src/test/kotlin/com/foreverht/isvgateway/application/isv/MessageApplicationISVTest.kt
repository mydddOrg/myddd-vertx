package com.foreverht.isvgateway.application.isv

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.MessageApplication
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

//@Disabled
class MessageApplicationISVTest : AbstractWorkPlusISVTest() {

    private val messageApplication by lazy { InstanceFactory.getInstance(MessageApplication::class.java,"WorkPlusApp") }


    @Test
    fun testSendFileMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
                val mapper = ObjectMapper().registerModule(KotlinModule())
                val messageD = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageD).await()
                testContext.verify { Assertions.assertTrue(success) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSendVoiceMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
                val mapper = ObjectMapper().registerModule(KotlinModule())
                val messageD = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageD).await()
                testContext.verify { Assertions.assertTrue(success) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSendTextMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
                val mapper = ObjectMapper().registerModule(KotlinModule())
                val messageD = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageD).await()
                testContext.verify { Assertions.assertTrue(success) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSendImageMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val imageMsgBodyJson = json {
                    obj(
                        "toUserList" to array("A","B"),
                        "forAll" to false,
                        "body" to obj(
                            "content" to "2123",
                            "mediaId" to UUID.randomUUID().toString(),
                            "height" to 100,
                            "width" to 150,
                            "msgType" to "IMAGE"
                        )
                    )
                }

                val mapper = ObjectMapper().registerModule(KotlinModule())
                val messageDTO = mapper.readValue(imageMsgBodyJson.toString(), MessageDTO::class.java)

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageDTO).await()
                testContext.verify { Assertions.assertTrue(success) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}