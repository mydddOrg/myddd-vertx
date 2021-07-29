package org.myddd.vertx.grpc

import com.google.protobuf.Empty
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.Status
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.config.Config

class GrpcHealthCheckVerticle: CoroutineVerticle() {

    private val discovery: ServiceDiscovery by lazy {
        ServiceDiscovery.create(vertx)
    }

    private val logger by lazy { LoggerFactory.getLogger(GrpcHealthCheckVerticle::class.java) }

    private val delay by lazy { Config.getLong("grpc.health.delay",5000) }

    override suspend fun start() {
        vertx.setPeriodic(delay){
            GlobalScope.launch(vertx.dispatcher()) {
                gRPCServiceHealthCheck()
            }
        }
        super.start()
    }

    private suspend fun gRPCServiceHealthCheck():Future<Unit>{
        return try{
            val records = discovery.getRecords({true},true).await()
            records.forEach {
                val grpcLocation = it.location.mapTo(GrpcLocation::class.java)

                val channel = VertxChannelBuilder
                    .forAddress(vertx, grpcLocation.host, grpcLocation.port)
                    .usePlaintext()
                    .build()
                try {
                    val stub = VertxHealthCheckGrpc.newVertxStub(channel)
                    val result = stub.hello(Empty.getDefaultInstance()).await()
                    if(result.value){
                        if(it.status == Status.OUT_OF_SERVICE){
                            makeServiceUp(it).await()
                        }
                        logger.debug("【gRPC Health Check OK】")
                        logger.debug(JsonObject.mapFrom(it))
                    }else{
                        makeServiceOutOfService(it)
                        logger.error("【gRPC Health Check Error】")
                        logger.error(JsonObject.mapFrom(it))
                    }
                }catch (t:Throwable){
                    makeServiceOutOfService(it)
                    logger.error("【gRPC Health Check Error】")
                    logger.error(JsonObject.mapFrom(it))
                    logger.error(t)
                }finally {
                    channel.shutdown()
                }

            }
            Future.succeededFuture()
        }catch (t:Throwable){
            logger.warn(t)
            Future.succeededFuture()
        }
    }

    private suspend fun makeServiceOutOfService(record:Record):Future<Unit>{
        return changeServiceStatus(record,Status.OUT_OF_SERVICE)
    }

    private suspend fun makeServiceUp(record: Record):Future<Unit>{
        return changeServiceStatus(record,Status.UP)
    }

    private suspend fun changeServiceStatus(record: Record,status: Status):Future<Unit>{
        return try {
            record.status = status
            discovery.update(record).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.succeededFuture()
        }
    }
    override suspend fun stop() {
        super.stop()
    }
}