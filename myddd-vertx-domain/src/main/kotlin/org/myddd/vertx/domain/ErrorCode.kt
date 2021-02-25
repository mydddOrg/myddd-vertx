package org.myddd.vertx.domain

interface ErrorCode {

    fun errorCode():String {
        return this.toString()
    }
}