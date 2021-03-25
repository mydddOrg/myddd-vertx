package com.foreverht.isvgateway.bootstrap.router

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class AppRouteTest: AbstractISVRouteTest() {

    private val logger by lazy { LoggerFactory.getLogger(AppRouteTest::class.java) }

    @Test
    fun testAppAdmins(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/v1/app/admins?accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                val errorResponse = webClient.get(port,host,"/v1/app/admins")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testAppDetail(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/v1/app/detail?accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                var errorResponse = webClient.get(port,host,"/v1/app/detail?accessToken=${UUID.randomUUID()}")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}