package org.myddd.vertx.grpc.local

import examples.GreeterGrpc
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
import kotlin.random.Random


@ExtendWith(VertxExtension::class)
class TestGreeterGrpc {


    companion object {
        private val randomPort = Random.nextInt(9090,10010)

        var service = object : GreeterVertxImplBase() {
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
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                val rpcServer: VertxServer = VertxServerBuilder
                    .forAddress(vertx, "localhost", randomPort)
                    .addService(service)
                    .build()

                rpcServer.start()
                testContext.completeNow()
            }
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