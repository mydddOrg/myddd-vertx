package com.foreverht.isvgateway

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RandomStringTest {

    @Test
    fun testRandomString(){
        val random = RandomStringUtils.random(32,true,true)
        println(random)
        Assertions.assertNotNull(random)
    }
}