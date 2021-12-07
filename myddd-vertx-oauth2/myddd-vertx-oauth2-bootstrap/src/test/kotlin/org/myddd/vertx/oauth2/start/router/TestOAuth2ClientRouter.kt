package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.*
import org.myddd.vertx.oauth2.start.AbstractWebTest

class TestOAuth2ClientRouter:AbstractWebTest(){

    @Test
    fun emptyTest(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify { Assertions.assertEquals(1,1) }
        testContext.completeNow()
    }

}