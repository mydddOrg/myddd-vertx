package org.myddd.vertx.grpc.local

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import examples.HelloReply
import examples.HelloRequest
import examples.VertxGreeterGrpc
import examples.VertxGreeterGrpc.GreeterVertxImplBase
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
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
import org.myddd.vertx.grpc.VertxHealthCheckGrpc
import kotlin.random.Random


@ExtendWith(VertxExtension::class)
class TestGreeterGrpc {


    companion object {
        private val randomPort = Random.nextInt(9090,10010)

        private val service = object : GreeterVertxImplBase() {
            override fun sayHello(request: HelloRequest): Future<HelloReply> {
                return Future.succeededFuture(
                    HelloReply.newBuilder()
                        .setMessage(request.name)
                        .build()
                )
            }
        }.withCompression("gzip")

        private val healthCheck = object : VertxHealthCheckGrpc.HealthCheckVertxImplBase() {
            override fun hello(request: Empty?): Future<BoolValue> {
                return Future.succeededFuture(BoolValue.of(true))
            }
        }


        @JvmStatic
        @BeforeAll
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                val rpcServer: VertxServer = VertxServerBuilder
                    .forAddress(vertx, "localhost", randomPort)
                    .addService(healthCheck)
                    .addService(service)
                    .build()

                rpcServer.start()
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testHeathCheck(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val channel = VertxChannelBuilder
                    .forAddress(vertx, "localhost", randomPort)
                    .usePlaintext()
                    .build()

                val stub = VertxHealthCheckGrpc.newVertxStub(channel)

                val health = stub.hello(Empty.getDefaultInstance()).await()

                testContext.verify {
                    Assertions.assertTrue(health.value)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSayHello(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val channel = VertxChannelBuilder
                    .forAddress(vertx, "localhost", randomPort)
                    .usePlaintext()
                    .build()
                val stub = VertxGreeterGrpc.newVertxStub(channel)
                val request = HelloRequest.newBuilder().setName("Julien").build()

                val result = stub.sayHello(request).await()
                testContext.verify {
                    Assertions.assertNotNull(result)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}