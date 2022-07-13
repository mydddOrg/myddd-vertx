package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.grpc.health.HealthCheckApplication
import org.myddd.vertx.grpc.health.HealthGrpcService
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.execute

@ExtendWith(VertxExtension::class)
class TestGrpcInstanceProvider {
    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcInstanceProvider::class.java) }

        private lateinit var deployId:String

        private val healthApplicationProxy by lazy {
            GrpcInstanceFactory.getInstance<VertxHealthCheckGrpc.HealthCheckVertxStub>(HealthGrpcService.HealthCheck)
        }

        private val vertx by lazy { Vertx.vertx() }

        private val guiceInstanceProvider by lazy {
                GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                    override fun configure() {
                        bind(Vertx::class.java).toInstance(vertx)
                        bind(GrpcInstanceProvider::class.java).to(ServiceDiscoveryGrpcInstanceProvider::class.java)

                        bind(HealthCheckApplication::class.java)
                    }
                }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.execute {
                deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                Assertions.assertNotNull(deployId)
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
}