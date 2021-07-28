package org.myddd.vertx.grpc

enum class SampleGrpcService:GrpcService {

    SampleCheck {
        override fun serviceClass(): Class<*> {
            return VertxHealthCheckGrpc::class.java
        }

        override fun stubClass(): Class<*> {
            return VertxHealthCheckGrpc.HealthCheckVertxStub::class.java
        }
    }

}