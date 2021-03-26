package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject

data class ImageMessageBody(
    var mediaId:String,
    var content:String,
    var height:Double,
    var width:Double,
    var isGif:Boolean = false
): AbstractMessageBody(msgType = IMAGE_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        TODO("Not yet implemented")
    }

    override fun weiXinBodyValue(): JsonObject {
        TODO("Not yet implemented")
    }
}