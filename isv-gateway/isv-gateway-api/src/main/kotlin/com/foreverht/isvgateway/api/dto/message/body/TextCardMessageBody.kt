package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

data class TextCardMessageBody(val title:String,val description:String,val url:String,val buttonText:String):AbstractMessageBody(msgType = TEXT_CARD_MSG_TYPE) {
    override fun weiXinBodyKey(): String {
        return "textcard"
    }

    override fun weiXinBodyValue(media: String?): JsonObject {
        return json {
            obj(
                "title" to title,
                "description" to description,
                "url" to url,
                "btntxt" to buttonText
            )
        }
    }

    override fun supportWorkPlus() = false


}