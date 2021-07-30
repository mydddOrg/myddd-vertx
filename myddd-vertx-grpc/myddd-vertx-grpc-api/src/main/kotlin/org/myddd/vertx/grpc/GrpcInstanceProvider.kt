package org.myddd.vertx.grpc

import io.vertx.core.Future

interface GrpcInstanceProvider {

    suspend fun getSignature(grpcService: GrpcService):Future<String>

    suspend fun <T> getService(grpcService: GrpcService):Future<Pair<T,String>>

    fun <T> getInstance(grpcService: GrpcService,lazyLoad:Boolean = false):ServiceProxy<T>
}