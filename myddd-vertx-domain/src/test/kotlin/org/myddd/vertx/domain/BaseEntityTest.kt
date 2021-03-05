package org.myddd.vertx.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BaseEntityTest {

    @Test
    fun testEntity(){
        val user = UserEntity()
        user.id = 10
        Assertions.assertEquals(10,user.getId())
    }

}