package org.myddd.vertx.grpc.health

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import io.vertx.core.Future
import org.myddd.vertx.config.Config
import org.myddd.vertx.grpc.BindingGrpcService
import org.myddd.vertx.grpc.GrpcService
import org.myddd.vertx.grpc.NodeInfo
import org.myddd.vertx.grpc.VertxHealthCheckGrpc

class HealthCheckApplication: VertxHealthCheckGrpc.HealthCheckVertxImplBase(), BindingGrpcService {

    override fun hello(request: Empty?): Future<BoolValue?> {
        return Future.succeededFuture(BoolValue.of(true))
    }

    override fun nodeInfo(request: Empty?): Future<NodeInfo> {
        return try {
            val nodeInfo = NodeInfo.newBuilder()
                .setHost(Config.getString("grpc.host"))
                .setPort(Config.getInteger("grpc.port"))
                .build()
            Future.succeededFuture(nodeInfo)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
    override fun grpcService(): GrpcService {
        return HealthGrpcService.HealthCheck
    }

}