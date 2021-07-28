package org.myddd.vertx.grpc

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import io.grpc.BindableService
import io.vertx.core.Future

class HealthGrpcBootstrapVerticle: GrpcBootstrapVerticle() {

    private val healthCheck = object : VertxHealthCheckGrpc.HealthCheckVertxImplBase(),BindingGrpcService {
        override fun hello(request: Empty?): Future<BoolValue?> {
            return Future.succeededFuture(BoolValue.of(true))
        }

        override fun grpcService(): GrpcService {
            return SampleGrpcService.HealthCheck
        }
    }


    override fun services(): List<BindableService> {
        return arrayListOf(healthCheck)
    }
}