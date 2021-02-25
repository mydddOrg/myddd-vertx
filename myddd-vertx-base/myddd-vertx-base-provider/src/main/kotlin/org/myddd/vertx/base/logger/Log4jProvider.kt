package org.myddd.vertx.base.logger

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Log4jProvider : LoggerProvider {

    override fun getLogger(clazz: Class<Any>): Logger {
        return LogManager.getLogger(clazz)
    }

}