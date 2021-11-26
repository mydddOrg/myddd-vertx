package org.myddd.vertx.oauth2

import org.myddd.vertx.error.ErrorCode

enum class OAuth2ApiErrorCode : ErrorCode {

    CLIENT_NOT_FOUND,

    CLIENT_SECRET_NOT_MATCH,

    CLIENT_DISABLED,

    CLIENT_TOKEN_NOT_FOUND,

    ACCESS_TOKEN_NOT_MATCH

}