package org.myddd.vertx.grpc

import io.vertx.core.Future

interface GrpcInstanceProvider {

    suspend fun <T> getService(grpcService: GrpcService):Future<T>

    fun <T> getInstance(grpcService: GrpcService,lazyLoad:Boolean = false):ServiceProxy<T>
}