package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.protobuf.Empty
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.grpc.health.HealthCheckApplication
import org.myddd.vertx.grpc.health.HealthGrpcService
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

@ExtendWith(VertxExtension::class)
@Disabled("此测试需要一定的时间")
class TestGrpcHealthCheckVerticle {

    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcBootstrapVerticle::class.java) }
        private lateinit var deployId:String
        private lateinit var healthCheckDeployId:String

        private val healthApplicationProxy by lazy {
            GrpcInstanceFactory.getInstance<VertxHealthCheckGrpc.HealthCheckVertxStub>(HealthGrpcService.HealthCheck)
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {

                    InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                        override fun configure() {
                            bind(Vertx::class.java).toInstance(vertx)
                            bind(GrpcInstanceProvider::class.java).to(ServiceDiscoveryGrpcInstanceProvider::class.java)

                            bind(HealthCheckApplication::class.java)
                        }
                    })))

                    deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                    Assertions.assertNotNull(deployId)

                    healthCheckDeployId = vertx.deployVerticle(GrpcHealthCheckVerticle()).await()
                    Assertions.assertNotNull(healthCheckDeployId)
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll(vertx: Vertx, testContext: VertxTestContext){
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

    @Test
    fun testGrpcBusEvent(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                healthApplicationProxy.rpcRun {
                    it.hello(Empty.getDefaultInstance())
                }.await()
                delay(1000 * 2)
                vertx.undeploy(deployId).await()
                delay(1000 * 5)

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGrpcHealthCheck(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                delay(1000 * 20)
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }



}