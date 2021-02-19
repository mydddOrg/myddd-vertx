package org.myddd.vertx.domain

/**
 * 业务逻辑异常
 */
class BusinessLogicException(errorCode:ErrorCode) : RuntimeException(errorCode.errorCode()) {

    val errorMsg:String? = errorCode.errorMsg()

    val errorCode:String = errorCode.errorCode()

}