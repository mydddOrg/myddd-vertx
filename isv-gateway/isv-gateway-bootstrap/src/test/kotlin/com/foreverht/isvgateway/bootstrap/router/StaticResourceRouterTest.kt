package com.foreverht.isvgateway.bootstrap.router

import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StaticResourceRouterTest : AbstractRouteTest() {

    companion object {
        private val logger = LoggerFactory.getLogger(StaticResourceRouterTest::class.java)
    }

    @Test
    fun testOasIndex(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/index.html")
                    .send().await()
                testContext.verify { Assertions.assertEquals(200,response.statusCode()) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}