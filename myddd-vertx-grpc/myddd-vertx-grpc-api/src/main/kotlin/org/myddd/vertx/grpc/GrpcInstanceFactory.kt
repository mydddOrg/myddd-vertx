package org.myddd.vertx.grpc

import io.vertx.core.Future
import io.vertx.core.Vertx
import org.myddd.vertx.ioc.InstanceFactory

object GrpcInstanceFactory {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val instanceProvider by lazy { InstanceFactory.getInstance(GrpcInstanceProvider::class.java) }

    suspend fun <T> getInstance(grpcService: GrpcService):Future<GrpcServiceProxy<T>> {
        return instanceProvider.getInstance(grpcService)
    }
}