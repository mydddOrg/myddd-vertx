package org.myddd.vertx.base

import org.myddd.vertx.error.ErrorCode

enum class SomeErrorCode : ErrorCode {

    SOME_ERROR,

    ANOTHER_ERROR {
        override fun errorCode(): String {
            return "OVERRIDE"
        }
    }

}