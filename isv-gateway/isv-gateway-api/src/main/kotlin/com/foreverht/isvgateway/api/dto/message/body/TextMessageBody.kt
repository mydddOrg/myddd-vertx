package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

data class TextMessageBody(
    var content:String
) : AbstractMessageBody(msgType = TEXT_MSG_TYPE) {

    override fun weiXinBodyKey(): String {
        return "text"
    }

    override fun weiXinBodyValue(): JsonObject {
        return json {
            obj(
                "content" to content
            )
        }
    }
}