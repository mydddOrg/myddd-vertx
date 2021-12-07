package org.myddd.vertx.domain

import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.domain.mock.MockBaseEntity

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestBaseEntity {
    @Test
    fun testEntity(){
        val user = MockBaseEntity()
        Assertions.assertTrue(user.getId() > 0)
    }
}