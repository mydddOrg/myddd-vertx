package org.myddd.vertx.base.logger

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Log4jProviderTest {

    private val loggerProvider: LoggerProvider = Log4jProvider()

    @Test
    fun testLog4jProvider(){
        val logger = loggerProvider.getLogger(Any::class.java)
        Assertions.assertNotNull(logger)
    }
}