package com.foreverht.isvgateway.domain

import org.myddd.vertx.error.ErrorCode

enum class ISVErrorCode : ErrorCode {

    /**
     * 不支持的clientType
     */
    CLIENT_TYPE_NOT_SUPPORT,

    CLIENT_ID_NOT_FOUND,

    SUITE_ID_NOT_FOUND

}