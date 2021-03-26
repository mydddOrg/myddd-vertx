package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject

data class VoiceMessageBody(
    val duration:Int,
    var mediaId:String
): AbstractMessageBody(msgType = VOICE_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        TODO("Not yet implemented")
    }

    override fun weiXinBodyValue(): JsonObject {
        TODO("Not yet implemented")
    }
}
