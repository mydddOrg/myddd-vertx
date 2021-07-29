package org.myddd.vertx.grpc

import io.grpc.ManagedChannel
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.AbstractServiceReference

class GrpcServiceReference(vertx: Vertx?,discovery: ServiceDiscovery?,record: Record?): AbstractServiceReference<ManagedChannel>(vertx,discovery,record) {

    override fun retrieve(): ManagedChannel {
        val grpcLocation = this.record().location.mapTo(GrpcLocation::class.java)

        return VertxChannelBuilder
            .forAddress(vertx, grpcLocation.host, grpcLocation.port)
            .usePlaintext()
            .build()
    }
}