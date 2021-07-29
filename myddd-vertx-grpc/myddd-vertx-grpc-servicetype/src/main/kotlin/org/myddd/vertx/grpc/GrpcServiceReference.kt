package org.myddd.vertx.grpc

import io.grpc.Channel
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.AbstractServiceReference

class GrpcServiceReference(vertx: Vertx?,discovery: ServiceDiscovery?,record: Record?): AbstractServiceReference<Any>(vertx,discovery,record) {

    override fun retrieve(): Any {
        val grpcLocation = this.record().location.mapTo(GrpcLocation::class.java)

        val channel = VertxChannelBuilder
            .forAddress(vertx, grpcLocation.host, grpcLocation.port)
            .usePlaintext()
            .build()

        val service = Class.forName(this.record().name)
        val method = service.getMethod("newVertxStub", Channel::class.java)
        return method.invoke(null, channel)
    }
}