package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MediaApplication
import com.foreverht.isvgateway.api.MessageApplication
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import com.foreverht.isvgateway.application.workplus.AbstractWorkPlusTest
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
import org.myddd.vertx.json.AsyncJsonMapper
import java.util.*

@Disabled("调用了真实的微信，不要轻易执行此单元测试")
class MessageApplicationWorkWeiXinTest:AbstractWorkWeiXinTest(){

    private val messageApplication by lazy { InstanceFactory.getInstance(MessageApplication::class.java, WORK_WEI_XIN) }
    private val mediaApplication by lazy { InstanceFactory.getInstance(MediaApplication::class.java, WORK_WEI_XIN) }

    @Test
    fun testSendFileMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val path = MediaApplicationWorkWeiXinTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaId = mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }


                val msgJsonObject = json {
                    obj(
                        "toUserList" to array("LiuLin"),
                        "forAll" to false,
                        "body" to obj(
                            "name" to "aaa.mp3",
                            "size" to 1421312,
                            "mediaId" to mediaId,
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
    fun testSendImageMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()){
            try {
                val path = MediaApplicationWorkWeiXinTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaId = mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }

                val msgJsonObject = json {
                    obj(
                        "toUserList" to array("LiuLin"),
                        "forAll" to false,
                        "body" to obj(
                            "mediaId" to mediaId,
                            "msgType" to "IMAGE"
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
    fun testSendTextMessage(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val msgJsonObject = json {
                    obj(
                        "toUserList" to array("LiuLin"),
                        "forAll" to false,
                        "body" to obj(
                            "content" to "2123",
                            "msgType" to "TEXT"
                        )
                    )
                }

                val messageDTO = AsyncJsonMapper.mapFrom(vertx,msgJsonObject.toString(), MessageDTO::class.java).await()

                val success = messageApplication.sendMessage(isvAccessToken = isvAccessToken,messageDTO).await()
                testContext.verify { Assertions.assertTrue(success) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}