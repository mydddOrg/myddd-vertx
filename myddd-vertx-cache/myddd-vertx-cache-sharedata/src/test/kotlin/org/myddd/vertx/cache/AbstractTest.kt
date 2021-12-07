package org.myddd.vertx.cache

import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
abstract class AbstractTest {

    companion object{
    }
}