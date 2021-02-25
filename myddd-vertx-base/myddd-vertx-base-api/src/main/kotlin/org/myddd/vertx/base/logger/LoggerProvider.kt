package org.myddd.vertx.base.logger

import org.apache.logging.log4j.Logger

interface LoggerProvider {

    fun getLogger(clazz: Class<Any>): Logger
}