package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.protobuf.Empty
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

@ExtendWith(VertxExtension::class)
class TestGrpcInstanceProvider {


    companion object {

        private val serviceProxy by lazy {
            GrpcInstanceFactory.getInstance<VertxHealthCheckGrpc.HealthCheckVertxStub>(SampleGrpcService.HealthCheck)
        }

        private lateinit var deployId:String

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                        override fun configure() {
                            bind(Vertx::class.java).toInstance(vertx)
                            bind(GrpcInstanceProvider::class.java).to(ServiceDiscoveryGrpcInstanceProvider::class.java)
                        }
                    })))

                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        private suspend fun startRpcService(vertx: Vertx):Future<Unit>{
            return try {
                deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        private suspend fun stopRpcService(vertx: Vertx):Future<Unit>{
            return try {
                vertx.undeploy(deployId).await()
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    @Test
    fun testInstanceProvider(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    serviceProxy.grpcService().await().hello(Empty.getDefaultInstance()).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                //启动服务
                startRpcService(vertx).await()

                testContext.verify {
                    Assertions.assertNotNull(serviceProxy)
                }

                val sayHello = serviceProxy.grpcService().await().hello(Empty.getDefaultInstance()).await()

                testContext.verify {
                    Assertions.assertTrue(sayHello.value)
                }


            }catch (t:Throwable){
                testContext.failNow(t)
            }finally {
                stopRpcService(vertx).await()
            }
            testContext.completeNow()
        }
    }
}