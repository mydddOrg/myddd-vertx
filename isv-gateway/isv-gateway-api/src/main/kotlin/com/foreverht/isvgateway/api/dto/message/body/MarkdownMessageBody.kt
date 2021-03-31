package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

data class MarkdownMessageBody(val content:String):AbstractMessageBody(msgType = MARKDOWN_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        return "markdown"
    }

    override fun weiXinBodyValue(media: String?): JsonObject {
        return json {
            obj(
                "content" to content
            )
        }
    }

    override fun supportWorkPlus() = false
}
