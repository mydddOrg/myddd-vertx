package org.myddd.vertx.grpc

import io.grpc.Channel
import io.grpc.NameResolver
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.ServiceDiscovery
import org.myddd.vertx.ioc.InstanceFactory
import java.net.InetSocketAddress

class ServiceDiscoveryGrpcInstanceProvider:GrpcInstanceProvider {

    companion object {
        const val TYPE = "grpc"

        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val discovery by lazy { ServiceDiscovery.create(vertx) }
    }

    override suspend fun <T> getInstance(grpcService: GrpcService):Future<T> {
        return try {
            val records = discovery.getRecords{
                it.type.equals(TYPE).and(
                    it.name.equals(grpcService.serviceName())
                )
            }.await()

            if(records.isEmpty()){
                throw GrpcInstanceNotFoundException(grpcService.serviceName())
            }


            val socketList = records.map {  InetSocketAddress(it.location.getString("host"), it.location.getInteger("port"))}
            val nameResolverFactory: NameResolver.Factory = MultiAddressNameResolverFactory(socketList)

            val channel = VertxChannelBuilder.forTarget(grpcService.serviceName())
                .nameResolverFactory(nameResolverFactory)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build()

            val service = grpcService.serviceClass()

            val method = service.getMethod("newVertxStub", Channel::class.java)
            val stub = method.invoke(null,channel) as T
            Future.succeededFuture(stub)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }
}