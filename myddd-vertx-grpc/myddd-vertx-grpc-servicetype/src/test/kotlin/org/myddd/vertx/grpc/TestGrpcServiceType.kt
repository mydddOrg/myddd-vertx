package org.myddd.vertx.grpc

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceReference
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.execute
import kotlin.random.Random

class TestGrpcServiceType:AbstractTest() {

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
        fun beforeAll(testContext: VertxTestContext){
            testContext.execute {
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
            }
        }

    }

    @Test
    fun testCreateGrpcService(testContext: VertxTestContext){
        testContext.execute {
            val grpcEndpoint = GrpcEndpoint.createRecord("my-test", "127.0.0.1", randomPort)
            val record = discovery.publish(grpcEndpoint).await()
            testContext.verify {
                Assertions.assertNotNull(record)
            }
        }
    }

    @Test
    fun testQueryGrpcService(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            val name = VertxHealthCheckGrpc::class.java.name
            val grpcEndpoint = GrpcEndpoint.createRecord(name, "127.0.0.1", randomPort)
            discovery.publish(grpcEndpoint).await()

            val query = discovery.getRecord{
                it.name.equals(name)
            }.await()

            testContext.verify {
                Assertions.assertNotNull(query)
            }

            val grpcServiceReference: ServiceReference = discovery.getReference(query)
            val stub = grpcServiceReference.get<VertxHealthCheckGrpc.HealthCheckVertxStub>()
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
        }
    }


}