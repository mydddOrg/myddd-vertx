package org.myddd.vertx.grpc

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class GrpcServiceProxy<T>(private val grpcService: GrpcService) {

    private var service:T? = null

    private val grpcInstanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    suspend fun grpcService():Future<T>{
        return try {
            if(Objects.isNull(service)){
                retried().await()
            }
            Future.succeededFuture(service!!)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun <X> rpcRun(execute:(service:T)->Future<X>):Future<X>{
        return try {
            val t = grpcService().await()
            execute(t)
        }catch (t:Throwable){
            service = null
            Future.failedFuture(t)
        }
    }

    suspend fun lazyLoad():Future<Unit>{
        return retried()
    }

    private suspend fun retried():Future<Unit>{
        return try {
            this.service = grpcInstanceProvider.getInstance<T>(grpcService).await()
            if(Objects.isNull(this.service)){
                throw GrpcInstanceNotFoundException(grpcService.serviceName())
            }
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

}