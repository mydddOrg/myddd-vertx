package org.myddd.vertx.web

import org.myddd.vertx.base.BusinessLogicException

class SomethingErrorWithParamException(values: Array<String> = emptyArray()):BusinessLogicException(WebErrorCode.SOMETHING_ERROR_WITH_PARAM, values = values)