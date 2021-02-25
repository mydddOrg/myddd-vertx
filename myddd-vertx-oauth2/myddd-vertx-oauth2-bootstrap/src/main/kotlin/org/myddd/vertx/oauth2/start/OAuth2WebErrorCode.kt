package org.myddd.vertx.oauth2.start

import org.myddd.vertx.domain.ErrorCode
import java.util.*

enum class OAuth2WebErrorCode :ErrorCode  {

    ILLEGAL_PARAMETER_FOR_CREATE_CLIENT,

    ILLEGAL_PARAMETER_FOR_CLIENT_ID_AND_CLIENT_SECRET,

    CLIENT_SECRET_NOT_MATCH,

    CLIENT_NOT_FOUND,

    NOT_SUPPORT_OAUTH2_GRANT_TYPE,

    ILLEGAL_PARAMETER_FOR_REFRESH_TOKEN,

    ILLEGAL_PARAMETER_FOR_REVOKE_TOKEN
}