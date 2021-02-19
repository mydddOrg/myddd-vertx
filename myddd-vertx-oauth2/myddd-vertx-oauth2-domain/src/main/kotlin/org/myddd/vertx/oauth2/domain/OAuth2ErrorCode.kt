package org.myddd.vertx.oauth2.domain

import org.myddd.vertx.domain.ErrorCode
import java.util.*

enum class OAuth2ErrorCode : ErrorCode{

    CLIENT_ID_CAN_NOT_NULL {
        override fun errorMsg(locale: Locale): String? {
            return "Client Id不能为空"
        }
    },

    CLIENT_NAME_CAN_NOT_NULL {
        override fun errorMsg(locale: Locale): String? {
            return "Client Name不能为空"
        }
    },

    ACCESS_TOKEN_NOT_EXISTS {
        override fun errorMsg(locale: Locale): String? {
            return "accessToken不存在"
        }
    },

    REFRESH_TOKEN_NOT_MATCH {
        override fun errorMsg(locale: Locale): String? {
            return "refreshToken不匹配"
        }
    }
}