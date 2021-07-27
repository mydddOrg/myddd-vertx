package org.myddd.vertx.grpc

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceReference
import io.vertx.servicediscovery.spi.ServiceType

class GrpcServiceType:ServiceType {

    override fun name(): String {
        TODO("Not yet implemented")
    }

    override fun get(vertx: Vertx?, discovery: ServiceDiscovery?, record: Record?, configuration: JsonObject?): ServiceReference {
        TODO("Not yet implemented")
    }
}