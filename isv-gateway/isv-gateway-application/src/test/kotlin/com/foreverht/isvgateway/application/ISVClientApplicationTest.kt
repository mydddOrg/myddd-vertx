package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.ISVClientApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class ISVClientApplicationTest : AbstractTest() {

    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    @Test
    fun testInstanceFactory(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java) )
        }
        testContext.completeNow()
    }

    @Test
    fun testCreateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }

        }
    }

}