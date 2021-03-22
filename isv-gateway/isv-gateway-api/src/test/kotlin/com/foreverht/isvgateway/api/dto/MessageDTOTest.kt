package com.foreverht.isvgateway.api.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MessageDTOTest {

    @Test
    fun testCreateFileMessageDTO(){
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
        val messageDTO = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)

        Assertions.assertNotNull(messageDTO)
        Assertions.assertEquals(AbstractMessageBody.FILE_MSG_TYPE,messageDTO.body.msgType)
    }

    @Test
    fun testCreateVoiceMessageDTO(){
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
        val messageDTO = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)
        Assertions.assertNotNull(messageDTO)
        Assertions.assertEquals(AbstractMessageBody.VOICE_MSG_TYPE,messageDTO.body.msgType)

    }

    @Test
    fun testCreateImageMessageDTO(){
        val msgJsonObject = json {
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
        val messageDTO = mapper.readValue(msgJsonObject.toString(), MessageDTO::class.java)
        Assertions.assertNotNull(messageDTO)
        Assertions.assertEquals(AbstractMessageBody.IMAGE_MSG_TYPE,messageDTO.body.msgType)
    }

    @Test
    fun testCreateTextMessageDTO(){
        val msgJsonObject = json {
            obj(
              "toUserList" to array("A","B"),
              "forAll" to true,
              "body" to obj(
                  "content" to "2123",
                  "msgType" to "TEXT"
              )
            )
        }

        val mapper = ObjectMapper().registerModule(KotlinModule())
        val textMessageDTO = mapper.readValue(msgJsonObject.toString(),MessageDTO::class.java)

        Assertions.assertNotNull(textMessageDTO)
        Assertions.assertEquals(AbstractMessageBody.TEXT_MSG_TYPE,textMessageDTO.body.msgType)

        Assertions.assertThrows(Exception::class.java){
            val textMsgBodyJson = json {
                obj(
                    "toUserList" to array("A","B"),
                    "forAll" to true,
                    "body" to obj(
                        "content" to "2123"
                    )
                )
            }
            val mapper = ObjectMapper().registerModule(KotlinModule())
            mapper.readValue(textMsgBodyJson.toString(),MessageDTO::class.java)
        }


    }
}