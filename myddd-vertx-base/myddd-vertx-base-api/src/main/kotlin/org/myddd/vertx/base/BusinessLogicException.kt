package org.myddd.vertx.base

interface BusinessLogicException {

    val errorCode:String

    val values: Array<String>

}