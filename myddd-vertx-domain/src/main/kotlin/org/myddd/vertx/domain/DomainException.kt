package org.myddd.vertx.domain

import org.myddd.vertx.base.BusinessLogicException

/**
 * 业务逻辑异常
 */
class DomainException(errorCode:ErrorCode, values: Array<String> = emptyArray()) : RuntimeException(errorCode.errorCode()),BusinessLogicException {

    override val errorCode:String = errorCode.errorCode()

    override val values: Array<String> = values

}