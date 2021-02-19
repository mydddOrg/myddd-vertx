package org.myddd.vertx.oauth2.application

import org.myddd.vertx.domain.ErrorCode
import java.util.*

enum class OAuth2ApiErrorCode : ErrorCode {

    CLIENT_NOT_FOUND {
        override fun errorMsg(locale: Locale): String? {
            return "没有找到对应的Client"
        }
    }
    ,

    CLIENT_SECRET_NOT_MATCH {
        override fun errorMsg(locale: Locale): String? {
            return "Client Secret不正确"
        }
    }
    ,

    CLIENT_DISABLED {
        override fun errorMsg(locale: Locale): String? {
            return "Client处于禁用状态"
        }
    },

    CLIENT_TOKEN_NOT_FOUND {
        override fun errorMsg(locale: Locale): String? {
            return "未找到TOKEN"
        }
    },

    ACCESS_TOKEN_NOT_MATCH {
        override fun errorMsg(locale: Locale): String? {
            return "accessToken无法匹配"
        }
    }

}