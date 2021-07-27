package org.myddd.vertx.grpc

import java.beans.ConstructorProperties

data class GrpcLocation @ConstructorProperties(value = ["host","port"])constructor(val host:String, val port:Int)
