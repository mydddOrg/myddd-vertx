package org.myddd.vertx.grpc

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceReference

class GrpcServiceType:GrpcEndpoint {

    override fun name(): String {
        return GrpcEndpoint.TYPE
    }

    override fun get(vertx: Vertx?, discovery: ServiceDiscovery?, record: Record?, configuration: JsonObject?): ServiceReference {
        return GrpcServiceReference(vertx,discovery,record)
    }
}