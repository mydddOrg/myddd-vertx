package org.myddd.vertx.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.mock.MockBaseAutoIDEntity


class TestBaseAutoIDEntity:AbstractDomainTest() {

    @Test
    fun testEntity(){
        val user = MockBaseAutoIDEntity()
        Assertions.assertTrue(user.getId() == 0L)
    }
}