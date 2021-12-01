package org.myddd.vertx.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.domain.mock.MockDocumentEntity
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.execute

@ExtendWith(VertxExtension::class)
class TestDocumentEntity {

    companion object {

        private val idGenerator by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }

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

        fun randomString():String{
            return idGenerator.nextId()
        }
    }

    @Test
    fun testDocumentEntity(testContext: VertxTestContext){
        testContext.execute {
            val mockDocumentEntity = MockDocumentEntity()
            mockDocumentEntity.name = randomString()
            mockDocumentEntity.id = randomString()

            testContext.verify {
                Assertions.assertThat(mockDocumentEntity.id).isNotNull
                Assertions.assertThat(mockDocumentEntity.name).isNotNull
            }

            val jsonObject = JsonObject.mapFrom(mockDocumentEntity)

            testContext.verify {
                Assertions.assertThat(jsonObject.getString("_id")).isNotNull()
            }
        }

    }

}