package org.myddd.vertx.oauth2

import org.myddd.vertx.base.BusinessLogicException

class AccessTokenNotMatchException:BusinessLogicException(OAuth2ApiErrorCode.ACCESS_TOKEN_NOT_MATCH)