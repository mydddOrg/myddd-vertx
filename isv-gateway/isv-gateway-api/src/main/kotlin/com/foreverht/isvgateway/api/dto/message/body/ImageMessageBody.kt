package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

data class ImageMessageBody(
    var mediaId:String,
    var content:String? = "",
    var height:Double = 100.0,
    var width:Double = 100.0,
    var isGif:Boolean = false
): AbstractMessageBody(msgType = IMAGE_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        return "image"
    }

    override fun weiXinBodyValue(mediaId:String?): JsonObject {
        return json {
            obj(
                "media_id" to mediaId
            )
        }

    }
}