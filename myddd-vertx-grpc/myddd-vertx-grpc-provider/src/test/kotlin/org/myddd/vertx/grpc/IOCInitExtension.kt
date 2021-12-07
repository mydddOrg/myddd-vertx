package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.myddd.vertx.grpc.health.HealthCheckApplication
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Vertx::class.java).toInstance(Vertx.vertx())
                bind(GrpcInstanceProvider::class.java).to(ServiceDiscoveryGrpcInstanceProvider::class.java)
                bind(HealthCheckApplication::class.java)
            }
        })))
    }
}