package org.myddd.vertx.grpc

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.random.Random

@ExtendWith(VertxExtension::class)
class TestGrpcServiceType {

    companion object {
        private lateinit var discovery: ServiceDiscovery

        private val logger by lazy { LoggerFactory.getLogger(TestServiceType::class.java) }

        private val randomPort = Random.nextInt(9090,10010)

        private val healthCheck = object : VertxHealthCheckGrpc.HealthCheckVertxImplBase() {
            override fun hello(request: Empty?): Future<BoolValue?>{
                return Future.succeededFuture(BoolValue.of(true))
            }
        }

        private lateinit var rpcServer:VertxServer

        @JvmStatic
        @BeforeAll
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    discovery = ServiceDiscovery.create(vertx)
                    testContext.verify {
                        Assertions.assertNotNull(discovery)
                    }
                    logger.debug(VertxHealthCheckGrpc::class.java.name)

                    rpcServer = VertxServerBuilder
                        .forAddress(vertx, "localhost", randomPort)
                        .addService(healthCheck)
                        .build()

                    rpcServer.start()

                    testContext.completeNow()

                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }
        }

    }

    @Test
    fun testCreateGrpcService(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val grpcEndpoint = GrpcEndpoint.createRecord("my-test", "127.0.0.1", randomPort)
                val record = discovery.publish(grpcEndpoint).await()
                testContext.verify {
                    Assertions.assertNotNull(record)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryGrpcService(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val grpcEndpoint = GrpcEndpoint.createRecord("my-test", "127.0.0.1", randomPort)
                discovery.publish(grpcEndpoint).await()

                val query = discovery.getRecord{
                    it.name.equals("my-test")
                }.await()

                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                val grpcServiceReference: ServiceReference = discovery.getReference(query)
                val channel = grpcServiceReference.get<ManagedChannel>()
                val stub = VertxHealthCheckGrpc.newVertxStub(channel)
                var health = stub.hello(Empty.getDefaultInstance()).await()
                testContext.verify {
                    Assertions.assertTrue(health.value)
                }


                rpcServer.shutdown()

                try {
                    stub.hello(Empty.getDefaultInstance()).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                vertx.executeBlocking<Unit> { promise ->
                    rpcServer.start{
                        promise.complete()
                    }
                }.await()

                health = stub.hello(Empty.getDefaultInstance()).await()
                testContext.verify {
                    Assertions.assertTrue(health.value)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }


}