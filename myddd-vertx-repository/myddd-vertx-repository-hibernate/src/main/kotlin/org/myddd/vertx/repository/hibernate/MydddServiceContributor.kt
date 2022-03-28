package org.myddd.vertx.repository.hibernate

import io.vertx.core.Vertx
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.reactive.vertx.VertxInstance
import org.hibernate.reactive.vertx.impl.ProvidedVertxInstance
import org.hibernate.service.spi.ServiceContributor


class MydddServiceContributor: ServiceContributor {

    companion object {
        public lateinit var vertx: Vertx
    }

    override fun contribute(serviceRegistryBuilder: StandardServiceRegistryBuilder?) {
        serviceRegistryBuilder!!.addService(VertxInstance::class.java, ProvidedVertxInstance(vertx))
    }
}