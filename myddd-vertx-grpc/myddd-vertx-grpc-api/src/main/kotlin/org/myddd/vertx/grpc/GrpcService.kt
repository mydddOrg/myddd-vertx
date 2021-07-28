package org.myddd.vertx.grpc

interface GrpcService {

    fun serviceName():String {
        return serviceClass().name
    }

    fun serviceClass():Class<*>

    fun stubClass():Class<*>

}