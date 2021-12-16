package org.myddd.vertx.grpc

import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.ServiceDiscovery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.execute


class TestGrpcBootstrapVerticle:AbstractTest() {

    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcBootstrapVerticle::class.java) }
        private lateinit var deployId:String

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            testContext.execute {
                deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                Assertions.assertNotNull(deployId)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll(testContext: VertxTestContext){
            testContext.execute {
                try {
                    vertx.undeploy(deployId).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }
        }
    }



    @Test
    fun testGrpcBootstrapVerticle(testContext: VertxTestContext){
        testContext.execute {
            val discovery = ServiceDiscovery.create(vertx)

            val records = discovery.getRecords{
                it.type.equals("grpc")
            }.await()

            testContext.verify {
                Assertions.assertTrue(records.isNotEmpty())
                Assertions.assertEquals(1,records.size)
            }
        }
    }


}