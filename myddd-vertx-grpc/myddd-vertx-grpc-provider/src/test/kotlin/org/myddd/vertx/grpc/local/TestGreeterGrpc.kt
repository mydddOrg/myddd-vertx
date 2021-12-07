package org.myddd.vertx.grpc.local

import examples.HelloReply
import examples.HelloRequest
import examples.VertxGreeterGrpc
import examples.VertxGreeterGrpc.GreeterVertxImplBase
import io.vertx.core.Future
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.grpc.AbstractTest
import org.myddd.vertx.junit.execute
import kotlin.random.Random


class TestGreeterGrpc:AbstractTest() {


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

        @JvmStatic
        @BeforeAll
        fun beforeAll(testContext: VertxTestContext){
            testContext.execute {
                val rpcServer: VertxServer = VertxServerBuilder
                    .forAddress(vertx, "localhost", randomPort)
                    .addService(service)
                    .build()

                rpcServer.start()
            }
        }
    }

    @Test
    fun testSayHello(testContext: VertxTestContext){
        testContext.execute {
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
        }
    }

}