package org.myddd.vertx.base

import org.myddd.vertx.error.ErrorCode

open class BusinessLogicException(val errorCode:ErrorCode,val values: Array<String> = emptyArray()) : RuntimeException(errorCode.toString()) {

}