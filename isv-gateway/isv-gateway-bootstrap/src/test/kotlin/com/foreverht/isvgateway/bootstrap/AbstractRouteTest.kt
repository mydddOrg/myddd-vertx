package com.foreverht.isvgateway.bootstrap

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
abstract class AbstractRouteTest {

    companion object {

        lateinit var deployId:String

        const val port:Int = 8080

        const val host = "127.0.0.1"

        lateinit var webClient: WebClient

        suspend fun startVerticle(vertx: Vertx,testContext: VertxTestContext):Future<Unit>{
            return try {
                webClient = WebClient.create(vertx)
                deployId = vertx.deployVerticle(ISVBootstrapVerticle(port = port)).await()
                testContext.verify {
                    Assertions.assertNotNull(deployId)
                }
                Future.succeededFuture(Unit)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }

        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    startVerticle(vertx,testContext).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    vertx.undeploy(deployId).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }
    }
}