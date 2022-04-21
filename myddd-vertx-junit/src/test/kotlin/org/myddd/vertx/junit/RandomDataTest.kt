package org.myddd.vertx.junit

import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RandomDataTest:AbstractJunitTest() {

    @Test
    fun testRandomLong(testContext: VertxTestContext){
        testContext.execute {
            testContext.verify {
                Assertions.assertNotNull(randomLong())
            }
        }
    }

    @Test
    fun testRandomString(testContext: VertxTestContext){
        testContext.execute {
            testContext.verify {
                Assertions.assertNotNull(randomString())
            }
        }
    }

}