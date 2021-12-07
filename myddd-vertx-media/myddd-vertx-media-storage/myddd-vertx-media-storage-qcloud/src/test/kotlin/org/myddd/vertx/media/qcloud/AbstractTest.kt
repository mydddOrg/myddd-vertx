package org.myddd.vertx.media.qcloud

import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
abstract class AbstractTest {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }
        val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
    }

}