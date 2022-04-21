package org.myddd.vertx.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.mock.MockBaseEntity

class TestBaseEntity:AbstractDomainTest() {
    @Test
    fun testEntity(){
        val user = MockBaseEntity()
        Assertions.assertTrue(user.getId() > 0)
    }
}