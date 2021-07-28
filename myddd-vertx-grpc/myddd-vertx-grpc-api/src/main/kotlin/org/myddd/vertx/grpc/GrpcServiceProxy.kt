package org.myddd.vertx.grpc

class GrpcServiceProxy<T>(private val service:T) {

    fun service():T{
        return service
    }

}