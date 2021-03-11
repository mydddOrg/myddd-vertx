package com.foreverht.isvgateway.bootstrap

import org.myddd.vertx.error.ErrorCode

enum class ISVClientErrorCode : ErrorCode {
    CLIENT_NOT_EXISTS,

    ORGANIZATION_NOT_FOUND
}