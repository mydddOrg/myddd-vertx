package org.myddd.vertx.string

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RandomIDStringTest {

    private val randomIDString = RandomIDStringProvider()

    @Test
    fun testRandomId(){
        val randomString = randomIDString.randomString(32)
        Assertions.assertEquals(32,randomString.length)
    }
}