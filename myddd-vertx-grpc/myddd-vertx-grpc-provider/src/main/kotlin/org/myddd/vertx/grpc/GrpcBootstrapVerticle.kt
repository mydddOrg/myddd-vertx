package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.grpc.BindableService
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.json.JsonObject
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.Status
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import java.util.*

abstract class GrpcBootstrapVerticle: CoroutineVerticle() {

    private val discovery: ServiceDiscovery by lazy {
        ServiceDiscovery.create(vertx)
    }

    private lateinit var rpcServer: VertxServer

    abstract fun services():List<BindableService>

    companion object {
        private val grpcRecords:MutableList<Record> = mutableListOf()


        private const val DEFAULT_HOST = "127.0.0.1"

        private const val DEFAULT_PORT = 8090
    }

    override suspend fun start() {
        super.start()
        initGlobalConfig().await()
        vertx.executeBlocking<Unit> {
            initIOC()
            it.complete()
        }.await()

        startGrpcServer().await()
        publishToDiscovery().await()
    }

    override suspend fun stop() {
        super.stop()
        unPublishFromDiscovery().await()
        stopGrpcServer().await()
    }


    private suspend fun publishToDiscovery():Future<Unit>{
        return try {
            services().forEach { it ->
                val grpcService = (it as BindingGrpcService)
                val existsQuery = json {
                    obj(
                        "name" to grpcService.grpcService().serviceName(),
                        "type" to "grpc",
                        "location" to json {
                            obj(
                                "host" to host(),
                                "port" to port()
                            )
                        }
                    )
                }
                val existsRecord = discovery.getRecord{ record ->
                    record.name.equals(grpcService.grpcService().serviceName()).and(
                        record.type.equals("grpc").and(
                            record.location.getString("host").equals(host()).and(
                                record.location.getInteger("port").equals(port())
                            )
                        )
                    )
                }.await()
                if(Objects.isNull(existsRecord)){
                    val grpcEndpoint = GrpcEndpoint.createRecord(grpcService.grpcService().serviceName(), host(), port())
                    val record = discovery.publish(grpcEndpoint).await()
                    grpcRecords.add(record)
                }else{
                    if(existsRecord.status != Status.UP){
                        existsRecord.status = Status.UP
                        discovery.update(existsRecord).await()
                    }
                }
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun unPublishFromDiscovery():Future<Unit>{
        return try {
            grpcRecords.forEach {
                discovery.unpublish(it.registration).await()
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

    private fun initIOC(){
        val module = abstractModules(vertx)
        if(Objects.nonNull(module)){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(module)))
        }
    }


    private suspend fun initGlobalConfig(): Future<Unit> {
        return Config.loadGlobalConfig(vertx)
    }

    open fun abstractModules(vertx: Vertx): AbstractModule?{
        return null
    }



    private fun host():String {
        return Config.getString("grpc.host",DEFAULT_HOST)
    }

    private fun port():Int {
        return Config.getInteger("grpc.port", DEFAULT_PORT)
    }
}