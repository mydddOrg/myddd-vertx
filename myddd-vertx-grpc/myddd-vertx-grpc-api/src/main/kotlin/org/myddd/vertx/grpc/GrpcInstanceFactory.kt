package org.myddd.vertx.grpc

import org.myddd.vertx.ioc.InstanceFactory


object GrpcInstanceFactory {

    private val grpcInstanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    fun <T> getInstance(grpcService: GrpcService,lazyLoad:Boolean = false):ServiceProxy<T> {
        return grpcInstanceProvider.getInstance(grpcService,lazyLoad)
    }
}