package com.foreverht.isvgateway.bootstrap.validation

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import java.util.*

@ExtendWith(VertxExtension::class)
class MessageValidationHandlerTest {

    companion object {
        private lateinit var messageValidationHandler:MessageValidationHandler

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(vertx)
                }
            })))

            messageValidationHandler = MessageValidationHandler()
            testContext.completeNow()
        }
    }

    @Test
    fun testMessageBodyValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val messageValidation = messageValidationHandler.messageSchema.build(messageValidationHandler.schemaParser)
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

                messageValidation.validateAsync(msgJsonObject).await()
            }catch (t:Throwable){
                t.printStackTrace()
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testFileMessageBodyValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                var fileMessageValidationHandler = messageValidationHandler.fileMessageBody.build(
                    messageValidationHandler.schemaParser)

                val fileJson = json {
                    obj(
                        "msgType" to "FILE",
                        "mediaId" to UUID.randomUUID().toString(),
                        "size" to 111,
                        "name" to "abc.png"
                    )
                }

                fileMessageValidationHandler.validateAsync(fileJson).await()


                try {

                    var errorJson = json {
                        obj(
                            "size" to 111,
                            "name" to "abc.png"
                        )
                    }

                    fileMessageValidationHandler.validateAsync(errorJson).await()

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
    fun testVoiceMessageBodyValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val voiceMessageValidation = messageValidationHandler.voiceMessageBody.build(messageValidationHandler.schemaParser)
                val voiceJson = json {
                    obj(
                        "msgType" to "VOICE",
                        "mediaId" to UUID.randomUUID().toString(),
                        "duration" to 3
                    )
                }
                voiceMessageValidation.validateAsync(voiceJson).await()

                try {
                    var errorJson = json {
                        obj(
                            "duration" to 3
                        )
                    }
                    voiceMessageValidation.validateAsync(errorJson).await()
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
    fun testImageMessageBodyValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val imageMessageValidation = messageValidationHandler.imageMessageBody.build(messageValidationHandler.schemaParser)
                val imageJson = json {
                    obj(
                        "msgType" to "IMAGE",
                        "mediaId" to UUID.randomUUID().toString(),
                        "content" to UUID.randomUUID().toString(),
                        "height" to 100,
                        "width" to 1231.2
                    )
                }

                imageMessageValidation.validateAsync(imageJson).await()

                try {
                    var errorJson = json {
                        obj(
                            "content" to UUID.randomUUID().toString(),
                            "height" to 100,
                            "width" to 1231.2
                        )
                    }
                    imageMessageValidation.validateAsync(errorJson).await()
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
    fun testTextMessageBodyValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val textMessageValidation = messageValidationHandler.testMessageBody.build(messageValidationHandler.schemaParser)
                val textJson = json {
                    obj(
                        "msgType" to "TEXT",
                        "content" to UUID.randomUUID().toString()
                    )
                }

                textMessageValidation.validateAsync(textJson).await()

                try {
                    val errorJson = json {
                        obj(
                            "co" to 123
                        )
                    }
                    textMessageValidation.validateAsync(errorJson).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


}