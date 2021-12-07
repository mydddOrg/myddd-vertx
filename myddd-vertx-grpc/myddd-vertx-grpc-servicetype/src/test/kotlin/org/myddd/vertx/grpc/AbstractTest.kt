package org.myddd.vertx.grpc

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
abstract class AbstractTest {
    companion object {
        val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
    }
}