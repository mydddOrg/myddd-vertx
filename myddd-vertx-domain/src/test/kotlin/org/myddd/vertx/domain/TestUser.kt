package org.myddd.vertx.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
@ExtendWith(VertxExtension::class)
class TestUser {

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll(){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                }
            })))
        }
    }

    @Test
    fun testEntity(){
        val user = UserEntity()
        Assertions.assertTrue(user.getId() > 0)
    }
}