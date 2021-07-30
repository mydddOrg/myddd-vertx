package org.myddd.vertx.grpc

import org.myddd.vertx.ioc.InstanceFactory
import java.util.*


object GrpcInstanceFactory {

    private val services:MutableMap<String,ServiceProxy<*>> = mutableMapOf()

    private val grpcInstanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    fun <T> getInstance(grpcService: GrpcService,lazyLoad:Boolean = false):ServiceProxy<T> {
        var exists = services[grpcService.serviceName()]
        if(Objects.isNull(exists)){
            exists = grpcInstanceProvider.getInstance<T>(grpcService,lazyLoad)
            services[grpcService.serviceName()] = exists
        }
        return exists as ServiceProxy<T>
    }
}