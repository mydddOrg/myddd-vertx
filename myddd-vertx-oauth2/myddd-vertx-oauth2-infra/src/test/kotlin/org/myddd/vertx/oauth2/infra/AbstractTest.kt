package org.myddd.vertx.oauth2.infra

import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
abstract class AbstractTest {
}