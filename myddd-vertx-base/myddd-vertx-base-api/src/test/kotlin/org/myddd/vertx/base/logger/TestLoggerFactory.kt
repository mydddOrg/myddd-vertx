package org.myddd.vertx.base.logger

import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.InstanceProvider

class TestLoggerFactory {

    private val loggerProvider = mock(LoggerProvider::class.java)

    private val instanceProvider: InstanceProvider = mock(InstanceProvider::class.java)

    @Test
    fun testGetLogger(){
        Mockito.`when`(instanceProvider.getInstance(LoggerProvider::class.java)).thenReturn(loggerProvider)
        Mockito.`when`(loggerProvider.getLogger(Any::class.java)).thenReturn(mock(Logger::class.java))

        InstanceFactory.setInstanceProvider(instanceProvider)

        val logger = LoggerFactory.getLogger(Any::class.java)
        Assertions.assertNotNull(logger)
    }
}