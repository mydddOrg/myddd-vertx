package org.myddd.vertx.oauth2.domain

import org.myddd.vertx.error.ErrorCode

enum class OAuth2ErrorCode : ErrorCode {

    CLIENT_ID_CAN_NOT_NULL,

    CLIENT_NAME_CAN_NOT_NULL,

    ACCESS_TOKEN_NOT_EXISTS,

    REFRESH_TOKEN_NOT_MATCH
}