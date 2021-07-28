package org.myddd.vertx.grpc

import io.grpc.Channel
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.ServiceDiscovery
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class DiscoveryGrpcInstanceProvider:GrpcInstanceProvider {

    companion object {
        const val TYPE = "grpc"

        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val discovery by lazy { ServiceDiscovery.create(vertx) }
    }

    override suspend fun <T> getInstance(grpcService: GrpcService):Future<GrpcServiceProxy<T>> {
        return try {
            val record = discovery.getRecord{
                it.type.equals(TYPE).and(
                    it.name.equals(grpcService.serviceName())
                )
            }.await()

            if(Objects.isNull(record)){
                throw GrpcInstanceNotFoundException()
            }

            val grpcLocation = record.location.mapTo(GrpcLocation::class.java)

            val channel = VertxChannelBuilder
                .forAddress(vertx, grpcLocation.host, grpcLocation.port)
                .usePlaintext()
                .build()

            val service = grpcService.serviceClass()

            val method = service.getMethod("newVertxStub", Channel::class.java)
            val stub = method.invoke(null,channel) as T
            Future.succeededFuture(GrpcServiceProxy(stub))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }
}