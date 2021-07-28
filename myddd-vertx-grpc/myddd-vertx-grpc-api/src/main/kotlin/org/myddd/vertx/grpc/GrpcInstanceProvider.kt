package org.myddd.vertx.grpc

import io.vertx.core.Future

interface GrpcInstanceProvider {

    suspend fun <T> getInstance(grpcService: GrpcService):Future<T>

}