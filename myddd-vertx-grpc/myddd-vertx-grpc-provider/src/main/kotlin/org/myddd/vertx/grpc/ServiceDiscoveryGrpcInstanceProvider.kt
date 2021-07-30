package org.myddd.vertx.grpc

import io.grpc.Channel
import io.grpc.NameResolver
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.ServiceDiscovery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import java.net.InetSocketAddress

class ServiceDiscoveryGrpcInstanceProvider:GrpcInstanceProvider {

    companion object {
        const val TYPE = "grpc"

        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val discovery by lazy { ServiceDiscovery.create(vertx) }

        private const val ROUND_ROBIN = "round_robin"
    }

    override suspend fun getSignature(grpcService: GrpcService):Future<String> {
        return try {
            val records = discovery.getRecords{
                it.type.equals(TYPE).and(
                    it.name.equals(grpcService.serviceName())
                )
            }.await()

            val signature = records.map { "${it.location.getString("host")}-${it.location.getString("port")}" }.joinToString { "::" }
            Future.succeededFuture(signature)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun <T> getService(grpcService: GrpcService):Future<Pair<T,String>> {
        return try {
            val records = discovery.getRecords{
                it.type.equals(TYPE).and(
                    it.name.equals(grpcService.serviceName())
                )
            }.await()

            val signature = records.map { "${it.location.getString("host")}-${it.location.getString("port")}" }.joinToString { "::" }

            if(records.isEmpty()){
                throw GrpcInstanceNotFoundException(grpcService.serviceName())
            }


            val socketList = records.map {  InetSocketAddress(it.location.getString("host"), it.location.getInteger("port"))}
            val nameResolverFactory: NameResolver.Factory = MultiAddressNameResolverFactory(socketList)

            val channel = VertxChannelBuilder.forTarget(grpcService.serviceName())
                .nameResolverFactory(nameResolverFactory)
                .defaultLoadBalancingPolicy(ROUND_ROBIN)
                .usePlaintext()
                .build()

            val service = grpcService.serviceClass()

            val method = service.getMethod("newVertxStub", Channel::class.java)
            val stub = method.invoke(null,channel) as T
            Future.succeededFuture(Pair(stub,signature))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override fun <T> getInstance(grpcService: GrpcService, lazyLoad: Boolean): ServiceProxy<T> {
        val serviceProxy = GrpcServiceProxy<T>(grpcService)
        if(!lazyLoad){
            GlobalScope.launch(vertx.dispatcher()) {
                serviceProxy.lazyLoad()
            }
        }
        return serviceProxy
    }
}