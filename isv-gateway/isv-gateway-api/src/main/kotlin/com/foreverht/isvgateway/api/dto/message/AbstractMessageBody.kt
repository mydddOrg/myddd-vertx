package com.foreverht.isvgateway.api.dto.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody.Companion.FILE_MSG_TYPE
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody.Companion.IMAGE_MSG_TYPE
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody.Companion.TEXT_MSG_TYPE
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody.Companion.VOICE_MSG_TYPE
import com.foreverht.isvgateway.api.dto.message.body.FileMessageBody
import com.foreverht.isvgateway.api.dto.message.body.ImageMessageBody
import com.foreverht.isvgateway.api.dto.message.body.TextMessageBody
import com.foreverht.isvgateway.api.dto.message.body.VoiceMessageBody

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "msgType")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = TextMessageBody::class, name = TEXT_MSG_TYPE),
    JsonSubTypes.Type(value = ImageMessageBody::class, name = IMAGE_MSG_TYPE),
    JsonSubTypes.Type(value = VoiceMessageBody::class, name = VOICE_MSG_TYPE),
    JsonSubTypes.Type(value = FileMessageBody::class, name = FILE_MSG_TYPE),

])
abstract class AbstractMessageBody(val msgType:String) {

    companion object {
        const val TEXT_MSG_TYPE = "TEXT"
        const val IMAGE_MSG_TYPE = "IMAGE"
        const val VOICE_MSG_TYPE = "VOICE"
        const val FILE_MSG_TYPE  = "FILE"
    }

}