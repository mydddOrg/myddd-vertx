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
    }
}