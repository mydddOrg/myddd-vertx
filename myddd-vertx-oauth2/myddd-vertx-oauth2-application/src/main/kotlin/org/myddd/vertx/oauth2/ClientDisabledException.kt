package org.myddd.vertx.oauth2

import org.myddd.vertx.base.BusinessLogicException

class ClientDisabledException:BusinessLogicException(OAuth2ApiErrorCode.CLIENT_DISABLED)
