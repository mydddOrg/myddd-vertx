package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody

data class TextMessageBody(
    var content:String
) : AbstractMessageBody(msgType = TEXT_MSG_TYPE)