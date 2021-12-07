package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
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
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.grpc.health.HealthCheckApplication
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.execute


class TestGrpcBootstrapVerticle:AbstractTest() {

    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcBootstrapVerticle::class.java) }
        private lateinit var deployId:String

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