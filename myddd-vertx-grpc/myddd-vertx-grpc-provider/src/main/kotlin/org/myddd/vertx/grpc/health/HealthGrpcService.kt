package org.myddd.vertx.grpc.health

import org.myddd.vertx.grpc.GrpcService
import org.myddd.vertx.grpc.VertxHealthCheckGrpc

enum class HealthGrpcService: GrpcService {

    HealthCheck {
        override fun serviceClass(): Class<*> {
            return VertxHealthCheckGrpc::class.java
        }

        override fun stubClass(): Class<*> {
            return VertxHealthCheckGrpc.HealthCheckVertxStub::class.java
        }
    }

}