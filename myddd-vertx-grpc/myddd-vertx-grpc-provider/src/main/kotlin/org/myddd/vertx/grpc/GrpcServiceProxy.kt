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
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

class GrpcServiceProxy<T>(private val grpcService: GrpcService,private var signature:String = ""): ServiceProxy<T> {

    private var service:T? = null

    private val grpcInstanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    private var consumer = vertx.eventBus().consumer<JsonObject>("vertx.discovery.announce")

    private var lastTimestamp:Long = System.currentTimeMillis()

    private val heartbeat = Config.getLong("grpc.heartbeat",1000 * 60)

    companion object {
        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val logger by lazy { LoggerFactory.getLogger(GrpcServiceProxy::class.java) }
    }

    init {
        vertx.setPeriodic(heartbeat){
            GlobalScope.launch(vertx.dispatcher()) {
                heartBeat()
            }
        }
    }

    init {
        consumer.handler{
            GlobalScope.launch(vertx.dispatcher()) {
                val body = it.body()
                val name = body.getString("name")
                if(name.equals(grpcService.serviceName())){
                    logger.info("gRPC Node Changed")
                    logger.info(body)
                    retried()
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

    private suspend fun heartBeat() {
        if(System.currentTimeMillis() - lastTimestamp > heartbeat){
            val signature = grpcInstanceProvider.getSignature(grpcService).await()
            if(signature != this.signature){
                logger.warn("【心跳检测】- 服务有变更: ${grpcService.serviceName()}")
                retried()
            }else{
                logger.warn("【心跳检测】- 正常: ${grpcService.serviceName()}")
            }
        }
        lastTimestamp = System.currentTimeMillis()
    }

    override suspend fun lazyLoad():Future<Unit>{
        return retried()
    }

    private suspend fun retried():Future<Unit>{
        return try {
            val (service,signature) = grpcInstanceProvider.getService<T>(grpcService).await()
            closeChannel().await()
            this.service = service
            this.signature = signature
            if(Objects.isNull(this.service)){
                throw GrpcInstanceNotFoundException(grpcService.serviceName())
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private fun closeChannel():Future<Unit>{
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