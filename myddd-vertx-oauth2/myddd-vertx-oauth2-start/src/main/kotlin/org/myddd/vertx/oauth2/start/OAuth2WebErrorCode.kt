package org.myddd.vertx.oauth2.start

import org.myddd.vertx.domain.ErrorCode

enum class OAuth2WebErrorCode :ErrorCode  {

    ILLEGAL_PARAMETER_FOR_CREATE_CLIENT {
        override fun errorMsg(language: String): String? {
            return "参数非法，clientId或name不能为空"
        }
    }

}