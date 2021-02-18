package org.myddd.vertx.domain

interface ErrorCode {

    fun errorCode():String {
        return this.toString()
    }

    fun errorMsg(language:String = "zh_CN"):String? {
        return null
    }
}