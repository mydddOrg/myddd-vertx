package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

data class FileMessageBody(
    var name:String,
    var mediaId:String,
    var size:Long
) : AbstractMessageBody(msgType = FILE_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        return "file"
    }

    override fun weiXinBodyValue(mediaId: String?): JsonObject {
        return json {
            obj(
                "media_id" to mediaId
            )
        }
    }
}