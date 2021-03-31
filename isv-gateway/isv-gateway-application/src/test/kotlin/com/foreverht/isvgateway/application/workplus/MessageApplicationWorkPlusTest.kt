package com.foreverht.isvgateway.application.workplus

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
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.json.AsyncJsonMapper
import java.util.*

class MessageApplicationWorkPlusTest: AbstractWorkPlusTest() {

    private val messageApplication by lazy { InstanceFactory.getInstance(MessageApplication::class.java,"WorkPlusApp") }

    @Test
    fun testSendMarkdownMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val msgJsonObject = json {
                    obj(
                        "toUserList" to array("LiuLin"),
                        "forAll" to false,
                        "body" to obj(
                            "content" to "如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)",
                            "msgType" to "MARKDOWN"
                        )
                    )
                }

                val messageDTO = AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

                try {
                    messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageDTO).await()
                    testContext.failNow("不支持的TYPE，不可能到这里")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSendTextCardMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val msgJsonObject = json {
                    obj(
                        "toUserList" to array("LiuLin"),
                        "forAll" to false,
                        "body" to obj(
                            "title" to "title",
                            "description" to "<div class=\\\"gray\\\">2016年9月26日</div> <div class=\\\"normal\\\">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class=\\\"highlight\\\">请于2016年10月10日前联系行政同事领取</div>",
                            "url" to "https://lite.workplus.io",
                            "buttonText" to "更多",
                            "msgType" to "TEXTCARD"
                        )
                    )
                }

                val messageDTO = AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

                try {
                    messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageDTO).await()
                    testContext.failNow("不支持的TYPE，不可能到这里")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

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
                val messageD = AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

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

                val messageD = AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

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

                val messageD =AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

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

                val messageDTO = AsyncJsonMapper.mapFrom(vertx,imageMsgBodyJson.toString(), MessageDTO::class.java).await()

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageDTO).await()
                testContext.verify { Assertions.assertTrue(success) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }



}