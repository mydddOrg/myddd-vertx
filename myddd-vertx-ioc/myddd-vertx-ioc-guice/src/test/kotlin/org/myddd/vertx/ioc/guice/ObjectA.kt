package org.myddd.vertx.ioc.guice

import javax.inject.Inject

class ObjectA :InterfaceA {

    @Inject
    var objectB:ObjectB? = null

    override fun getB(): InterfaceB? {
        return objectB
    }

}