package org.myddd.vertx.reflections

import io.vertx.core.impl.logging.LoggerFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TestReflectionUtils {

    private val logger by lazy { LoggerFactory.getLogger(TestReflectionUtils::class.java) }

    @Test
    fun testScan(){
        val scanEntities = ReflectionUtils.scan("org.myddd.vertx")
        Assertions.assertThat(scanEntities).isNotEmpty

        logger.debug(scanEntities)
    }
}