package org.myddd.vertx.web

import org.myddd.vertx.base.BusinessLogicException

class SomethingErrorException:BusinessLogicException(WebErrorCode.SOMETHING_ERROR)