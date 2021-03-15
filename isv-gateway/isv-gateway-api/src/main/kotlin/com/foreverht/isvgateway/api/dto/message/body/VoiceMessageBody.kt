package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody

data class VoiceMessageBody(
    val duration:Int,
    var mediaId:String
): AbstractMessageBody(msgType = VOICE_MSG_TYPE)
