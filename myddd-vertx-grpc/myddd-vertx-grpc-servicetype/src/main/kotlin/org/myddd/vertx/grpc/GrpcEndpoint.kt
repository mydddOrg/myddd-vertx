package org.myddd.vertx.grpc

import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.spi.ServiceType
import java.util.*


interface GrpcEndpoint:ServiceType {

    companion object {
        const val TYPE = "grpc"

        fun createRecord(name: String, host: String, port: Int, metadata: JsonObject? = null): Record {
            val record: Record = Record()
                .setName(name)
                .setType(TYPE)
                .setLocation(
                    JsonObject.mapFrom(GrpcLocation(host = host,port = port))
                )
            record.metadata = JsonObject().put("randomId", UUID.randomUUID().toString())
            return record
        }
    }
}