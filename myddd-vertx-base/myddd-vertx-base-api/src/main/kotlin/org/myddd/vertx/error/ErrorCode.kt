package org.myddd.vertx.error

interface ErrorCode {

    fun errorCode():String {
        return this.toString()
    }

    fun errorStatus():Int {
        return -1
    }
}