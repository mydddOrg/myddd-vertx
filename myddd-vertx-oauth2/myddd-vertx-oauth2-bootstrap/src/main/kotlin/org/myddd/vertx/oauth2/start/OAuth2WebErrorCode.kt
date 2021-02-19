package org.myddd.vertx.oauth2.start

import org.myddd.vertx.domain.ErrorCode
import java.util.*

enum class OAuth2WebErrorCode :ErrorCode  {


    ILLEGAL_PARAMETER_FOR_CREATE_CLIENT {
        override fun errorMsg(locale: Locale): String? {
            return "参数非法，clientId或name不能为空"
        }
    },

    ILLEGAL_PARAMETER_FOR_CLIENT_ID_AND_CLIENT_SECRET {
        override fun errorMsg(locale: Locale): String? {
            return "参数非法，clientId或clientSecret不能为空"
        }
    },

    CLIENT_SECRET_NOT_MATCH {
        override fun errorMsg(locale: Locale): String? {
            return "不允许的请求,clientSecret错误"
        }
    },

    CLIENT_NOT_FOUND {
        override fun errorMsg(locale: Locale): String? {
            return "找不到对应的Client"
        }
    },

    NOT_SUPPORT_OAUTH2_GRANT_TYPE {
        override fun errorMsg(locale: Locale): String? {
            return "不支持的grantType,当前仅支持client_credentials模式"
        }
    },

    ILLEGAL_PARAMETER_FOR_REFRESH_TOKEN {
        override fun errorMsg(locale: Locale): String? {
            return "参数非法，clientId或refreshToken不能为空"
        }
    },

    ILLEGAL_PARAMETER_FOR_REVOKE_TOKEN {
        override fun errorMsg(locale: Locale): String? {
            return "参数非法，clientId或accessToken不能为空"
        }
    }
}