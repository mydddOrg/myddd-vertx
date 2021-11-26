package org.myddd.vertx.oauth2

import org.myddd.vertx.base.BusinessLogicException

class ClientNotFoundException:BusinessLogicException(OAuth2ApiErrorCode.CLIENT_NOT_FOUND)