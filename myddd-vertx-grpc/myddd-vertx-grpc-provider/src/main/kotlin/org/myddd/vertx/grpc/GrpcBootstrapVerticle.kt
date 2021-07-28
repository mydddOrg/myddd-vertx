package org.myddd.vertx.grpc

import io.grpc.BindableService
import io.grpc.Grpc
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import org.myddd.vertx.config.Config

abstract class GrpcBootstrapVerticle: CoroutineVerticle() {

    private lateinit var discovery: ServiceDiscovery
    private lateinit var rpcServer: VertxServer

    abstract fun services():List<BindableService>

    companion object {
        private val grpcRecords:MutableList<Record> = mutableListOf()

        private const val DEFAULT_HOST = "127.0.0.1"

        private const val DEFAULT_PORT = 8090
    }

    override suspend fun start() {
        super.start()
        startGrpcServer().await()
        startDiscovery().await()
    }

    override suspend fun stop() {
        super.stop()
        stopGrpcServer().await()
    }


    private suspend fun startDiscovery():Future<Unit>{
        return try {
            discovery = ServiceDiscovery.create(vertx)
            services().forEach {
                val grpcService = (it as BindingGrpc)
                val grpcEndpoint = GrpcEndpoint.createRecord(grpcService.service().serviceName(), host(), port())
                val record = discovery.publish(grpcEndpoint).await()
                grpcRecords.add(record)
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    private fun startGrpcServer():Future<Unit>{
        val promise = PromiseImpl<Unit>()

        val builder =  VertxServerBuilder
            .forAddress(vertx, host(), port())
        services().forEach {
            builder.addService(it)
        }
        rpcServer =  builder.build()
        rpcServer.start{
            if(it.succeeded()){
                promise.complete()
            }else{
                promise.fail(it.cause())
            }
        }
        return promise.future()
    }

    private fun stopGrpcServer():Future<Boolean>{
        val promise = Promise.promise<Boolean>()
        rpcServer.shutdown{
            if(it.succeeded()){
                promise.complete(true)
            }else{
                promise.fail(it.cause())
            }
        }
        return promise.future()
    }


    private fun host():String {
        return Config.getString("rpc.host",DEFAULT_HOST)
    }

    private fun port():Int {
        return Config.getInteger("rpc.port", DEFAULT_PORT)
    }
}