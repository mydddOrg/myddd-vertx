package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.repository.api.EntityRepository

class TestMydddGuiceModule {

    @Test
    fun test(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(MydddGuiceModule())))
        Assertions.assertNotNull(InstanceFactory.getInstance(EntityRepository::class.java))
    }
}