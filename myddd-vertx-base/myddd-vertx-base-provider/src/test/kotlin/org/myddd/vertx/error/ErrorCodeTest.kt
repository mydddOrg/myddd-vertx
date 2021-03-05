package org.myddd.vertx.error

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.base.SomeErrorCode

class ErrorCodeTest {

    @Test
    fun testErrorCode(){
        Assertions.assertEquals("SOME_ERROR",SomeErrorCode.SOME_ERROR.errorCode())
        Assertions.assertEquals("OVERRIDE",SomeErrorCode.ANOTHER_ERROR.errorCode())
    }
}