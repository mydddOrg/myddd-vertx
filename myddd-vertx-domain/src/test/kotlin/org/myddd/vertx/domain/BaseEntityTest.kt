package org.myddd.vertx.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

class BaseEntityTest {

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
        user.id = 10
        Assertions.assertEquals(10,user.getId())
    }

}