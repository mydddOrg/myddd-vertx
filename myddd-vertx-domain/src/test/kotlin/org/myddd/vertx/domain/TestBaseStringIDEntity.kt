package org.myddd.vertx.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.mock.MockBaseStringIDEntity
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

class TestBaseStringIDEntity {

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll(){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                }
            })))
        }
    }

    @Test
    fun testCreatePerson(){
        val person = MockBaseStringIDEntity()
        Assertions.assertThat(person.id).isNotNull
    }
}