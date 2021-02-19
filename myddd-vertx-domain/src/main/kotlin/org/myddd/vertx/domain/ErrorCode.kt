package org.myddd.vertx.domain

import java.util.*

interface ErrorCode {

    fun errorCode():String {
        return this.toString()
    }

    fun errorMsg(locale :Locale = Locale.CHINESE):String? {
        return null
    }
}