package org.myddd.vertx.grpc.health

import com.google.protobuf.Empty
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.grpc.*
import org.myddd.vertx.junit.execute

class TestHealthCheckApplication:AbstractTest() {

    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcBootstrapVerticle::class.java) }
        private lateinit var deployId:String

        private val healthApplicationProxy by lazy {
            GrpcInstanceFactory.getInstance<VertxHealthCheckGrpc.HealthCheckVertxStub>(HealthGrpcService.HealthCheck)
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                    Assertions.assertNotNull(deployId)
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll(testContext: VertxTestContext){
            testContext.execute {
                vertx.undeploy(deployId).await()
            }
        }
    }

    @Test
    fun testHealthApplicationNotNull(testContext: VertxTestContext){
        testContext.execute {
            testContext.verify {
                Assertions.assertNotNull(healthApplicationProxy)
            }
        }
    }

    @Test
    fun testHealthApplicationHello(testContext: VertxTestContext){
        testContext.execute {
            val success = healthApplicationProxy.rpcRun {
                it.hello(Empty.getDefaultInstance())
            }.await()

            testContext.verify {
                Assertions.assertTrue(success.value)
            }
        }
    }

    @Test
    fun testHealthApplicationNodeInfo(testContext: VertxTestContext){
        testContext.execute {
            val nodeInfo = healthApplicationProxy.rpcRun {
                it.nodeInfo(Empty.getDefaultInstance())
            }.await()

            testContext.verify {
                Assertions.assertNotNull(nodeInfo)
            }
        }
    }


}