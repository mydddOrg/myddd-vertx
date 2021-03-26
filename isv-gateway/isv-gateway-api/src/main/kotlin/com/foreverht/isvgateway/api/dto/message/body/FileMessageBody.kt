package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject

data class FileMessageBody(
    var name:String,
    var mediaId:String,
    var size:Long
) : AbstractMessageBody(msgType = FILE_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        TODO("Not yet implemented")
    }

    override fun weiXinBodyValue(): JsonObject {
        TODO("Not yet implemented")
    }
}