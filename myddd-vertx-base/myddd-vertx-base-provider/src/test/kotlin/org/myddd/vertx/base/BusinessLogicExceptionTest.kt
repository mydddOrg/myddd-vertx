package org.myddd.vertx.base

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BusinessLogicExceptionTest {

    @Test
    fun testException(){
        val error = BusinessLogicException(errorCode = SomeErrorCode.SOME_ERROR)
        Assertions.assertTrue(error.errorCode == SomeErrorCode.SOME_ERROR)

        val withValueError = BusinessLogicException(errorCode = SomeErrorCode.SOME_ERROR, arrayOf("HAHA"))
        Assertions.assertTrue(withValueError.values.isNotEmpty())
    }
}