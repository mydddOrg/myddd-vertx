package org.myddd.vertx.oauth2

import org.myddd.vertx.error.ErrorCode

enum class OAuth2ErrorCode : ErrorCode {

    ACCESS_TOKEN_NOT_EXISTS,

    REFRESH_TOKEN_NOT_MATCH
}