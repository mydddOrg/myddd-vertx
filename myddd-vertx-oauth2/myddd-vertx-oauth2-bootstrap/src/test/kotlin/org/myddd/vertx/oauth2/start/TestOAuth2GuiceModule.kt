package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.repository.api.EntityRepository
@ExtendWith(VertxExtension::class)
class TestOAuth2GuiceModule {

    @Test
    fun test(vertx:Vertx){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(OAuth2GuiceModule(vertx))))
        Assertions.assertNotNull(InstanceFactory.getInstance(EntityRepository::class.java))
    }
}