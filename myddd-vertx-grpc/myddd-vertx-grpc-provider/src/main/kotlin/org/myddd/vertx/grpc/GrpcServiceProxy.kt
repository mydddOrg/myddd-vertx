package org.myddd.vertx.grpc

import io.grpc.ManagedChannel
import io.grpc.stub.AbstractStub
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import java.util.concurrent.TimeUnit

class GrpcServiceProxy<T>(private val grpcService: GrpcService): ServiceProxy<T> {

    private var service:T? = null

    private val grpcInstanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    private var consumer = vertx.eventBus().consumer<JsonObject>("vertx.discovery.announce")

    companion object {
        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val logger by lazy { LoggerFactory.getLogger(GrpcServiceProxy::class.java) }
    }

    init {
        consumer.handler{
            GlobalScope.launch(vertx.dispatcher()) {
                val body = it.body()
                val name = body.getString("name")
                if(name.equals(grpcService.serviceName())){
                    logger.info("gRPC Node Changed")
                    logger.info(body)
                    retried().await()
                }
            }
        }
    }

    override suspend fun grpcService():Future<T>{
        return try {
            if(Objects.isNull(service)){
                retried().await()
            }
            Future.succeededFuture(service!!)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun <X> rpcRun(execute:(service:T)->Future<X>):Future<X>{
        return try {
            val t = grpcService().await()
            execute(t)
        }catch (t:Throwable){
            service = null
            Future.failedFuture(t)
        }
    }

    override suspend fun lazyLoad():Future<Unit>{
        return retried()
    }

    private suspend fun retried():Future<Unit>{
        return try {
            closeChannel().await()
            this.service = grpcInstanceProvider.getService<T>(grpcService).await()
            if(Objects.isNull(this.service)){
                throw GrpcInstanceNotFoundException(grpcService.serviceName())
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun closeChannel():Future<Unit>{
        return try {
            if(Objects.nonNull(this.service)){
                val stub = this.service as AbstractStub<*>
                val channel = stub.channel as ManagedChannel
                try {
                    channel.shutdown().awaitTermination(30,TimeUnit.SECONDS)
                }catch (e:Exception){
                    logger.warn("关闭gRPC连接失败,",e)
                }
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}