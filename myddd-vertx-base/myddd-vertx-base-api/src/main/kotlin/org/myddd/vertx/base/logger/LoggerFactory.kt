package org.myddd.vertx.base.logger

import org.apache.logging.log4j.Logger
import org.myddd.vertx.ioc.InstanceFactory

class LoggerFactory {

    companion object {

        private val loggerProvider : LoggerProvider by lazy { InstanceFactory.getInstance(LoggerProvider::class.java)}

        fun getLogger(clazz: Class<Any>):Logger {
            return loggerProvider.getLogger(clazz)
        }

    }

}