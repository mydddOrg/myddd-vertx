package org.myddd.vertx.grpc

import io.grpc.BindableService
import org.myddd.vertx.grpc.health.HealthCheckApplication

class HealthGrpcBootstrapVerticle: GrpcBootstrapVerticle() {

    override fun services(): List<BindableService> {
        return arrayListOf(
            HealthCheckApplication()
        )
    }
}