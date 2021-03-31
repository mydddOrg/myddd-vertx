package com.foreverht.isvgateway.application.extention

import org.myddd.vertx.media.domain.Media


val imageType:Array<String> = arrayOf("png","jpg")
val voiceType:Array<String> = arrayOf("amr")
val videoType:Array<String> = arrayOf("mp4")

fun Media.type():String {
    val name = this.name.toLowerCase()
    val suffix = if (name.contains(".")) name.substring(name.lastIndexOf(".")+1) else ""
    return when {
        imageType.contains(suffix) -> "image"
        voiceType.contains(suffix) -> "voice"
        videoType.contains(suffix) -> "video"
        else -> "file"
    }
}