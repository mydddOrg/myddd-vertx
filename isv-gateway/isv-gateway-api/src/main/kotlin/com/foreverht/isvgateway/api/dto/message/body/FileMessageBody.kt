package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody

data class FileMessageBody(
    var name:String,
    var mediaId:String,
    var size:Long
) : AbstractMessageBody(msgType = FILE_MSG_TYPE)