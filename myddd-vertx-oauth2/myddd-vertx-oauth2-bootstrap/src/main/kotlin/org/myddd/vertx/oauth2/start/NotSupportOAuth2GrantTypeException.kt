package org.myddd.vertx.oauth2.start

import org.myddd.vertx.base.BusinessLogicException

class NotSupportOAuth2GrantTypeException:BusinessLogicException(OAuth2WebErrorCode.NOT_SUPPORT_OAUTH2_GRANT_TYPE)