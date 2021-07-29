package org.myddd.vertx.grpc

import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.spi.ServiceType
import java.util.*


interface GrpcEndpoint:ServiceType {

    companion object {
        const val TYPE = "grpc"

        const val GRPC_HEALTH = "grpc_health"

        fun createHealthRecord(name: String, host: String, port: Int): Record {
            return Record()
                .setName(name)
                .setType(GRPC_HEALTH)
                .setLocation(
                    JsonObject.mapFrom(GrpcLocation(host = host, port = port))
                )
        }

        fun createRecord(name: String, host: String, port: Int, metadata: JsonObject? = null): Record {
            val record: Record = Record()
                .setName(name)
                .setType(TYPE)
                .setLocation(
                    JsonObject.mapFrom(GrpcLocation(host = host,port = port))
                )
            if(Objects.nonNull(metadata)){
                record.metadata = metadata
            }
            return record
        }
    }
}