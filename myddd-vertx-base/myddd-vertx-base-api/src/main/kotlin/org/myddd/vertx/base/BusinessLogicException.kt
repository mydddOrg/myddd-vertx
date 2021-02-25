package org.myddd.vertx.base

import org.myddd.vertx.error.ErrorCode
import java.lang.RuntimeException

class BusinessLogicException(val errorCode:ErrorCode,val values: Array<String> = emptyArray()) : RuntimeException(errorCode.toString()) {

}