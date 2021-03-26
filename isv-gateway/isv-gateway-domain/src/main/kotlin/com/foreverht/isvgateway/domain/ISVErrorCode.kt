package com.foreverht.isvgateway.domain

import org.myddd.vertx.error.ErrorCode

enum class ISVErrorCode : ErrorCode {

    CLIENT_TYPE_NOT_SUPPORT,

    CLIENT_ID_NOT_FOUND,

    SUITE_ID_NOT_FOUND,

    SUITE_KEY_MISSING,

    REMOTE_CLIENT_TOKEN_REQUEST_FAIL,

    TEMPORARY_CODE_NOT_FOUND,

    PERMANENT_CODE_NOT_FOUND,

    SUITE_AUTH_NOT_FOUND,

    ACCESS_TOKEN_NOT_MATCH,

    ACCESS_TOKEN_INVALID,

    USER_ID_NOT_FOUND

}