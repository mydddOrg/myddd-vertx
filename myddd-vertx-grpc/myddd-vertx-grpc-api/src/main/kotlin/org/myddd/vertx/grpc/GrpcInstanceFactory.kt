package org.myddd.vertx.grpc

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory


object GrpcInstanceFactory {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    fun <T> getInstance(grpcService: GrpcService,lazyLoad:Boolean = false):GrpcServiceProxy<T> {
        val serviceProxy = GrpcServiceProxy<T>(grpcService)
        if(!lazyLoad){
            GlobalScope.launch(vertx.dispatcher()) {
                serviceProxy.lazyLoad()
            }
        }
        return serviceProxy
    }
}