package org.myddd.vertx.oauth2.start.router

import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.oauth2.start.AbstractWebTest

class TestOAuth2ClientRouter:AbstractWebTest(){

    @Test
    fun emptyTest(testContext: VertxTestContext){
        testContext.verify { Assertions.assertEquals(1,1) }
        testContext.completeNow()
    }

}