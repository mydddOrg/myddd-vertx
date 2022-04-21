package org.myddd.vertx.repository

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import org.myddd.vertx.repository.mongo.mock.MockMedia
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import java.util.*

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object :
                AbstractModule() {
                override fun configure() {
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    bind(MongoClient::class.java).toInstance(MongoClient.create(vertx, JsonObject()))
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

                    bind(DocumentEntityRepository::class.java).to(DocumentEntityRepositoryMongo::class.java)
                }
            }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.completeNow()
        }
    }

    protected fun randomMockDocumentEntity(): MockDocumentEntity {
        val random = MockDocumentEntity()
        random.name = UUID.randomUUID().toString()
        return random
    }

    protected fun randomMockMedia(): MockMedia {
        val random = MockMedia()
        random.digest = randomString()
        random.name = randomString()
        return random
    }
}