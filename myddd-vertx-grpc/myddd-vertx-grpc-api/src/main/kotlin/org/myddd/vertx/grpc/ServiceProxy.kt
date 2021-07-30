package org.myddd.vertx.grpc

import io.vertx.core.Future

interface ServiceProxy<T> {

    suspend fun grpcService(): Future<T>

    suspend fun <X> rpcRun(execute:(service:T)->Future<X>):Future<X>

    suspend fun lazyLoad():Future<Unit>
}