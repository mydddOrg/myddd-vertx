package org.myddd.vertx.grpc

import io.grpc.BindableService
import org.myddd.vertx.grpc.health.HealthCheckApplication
import org.myddd.vertx.ioc.InstanceFactory

class HealthGrpcBootstrapVerticle: GrpcBootstrapVerticle() {

    override fun services(): List<BindableService> {
        return arrayListOf(
            HealthCheckApplication(),
            InstanceFactory.getInstance(HealthCheckApplication::class.java)
        )
    }
}