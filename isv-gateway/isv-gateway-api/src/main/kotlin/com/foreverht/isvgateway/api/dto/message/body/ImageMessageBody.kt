package com.foreverht.isvgateway.api.dto.message.body

import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody

data class ImageMessageBody(
    var mediaId:String,
    var content:String,
    var height:Double,
    var width:Double,
    var isGif:Boolean = false
): AbstractMessageBody(msgType = IMAGE_MSG_TYPE)