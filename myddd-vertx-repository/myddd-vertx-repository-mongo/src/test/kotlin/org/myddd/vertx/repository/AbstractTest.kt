package org.myddd.vertx.repository

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import org.myddd.vertx.repository.mongo.mock.MockMedia
import java.util.*

@ExtendWith(VertxExtension::class, IOCInitExtension::class)
abstract class AbstractTest {

    val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

    companion object {

        private val idGenerate by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }

        val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

        val mongoClient by lazy { InstanceFactory.getInstance(MongoClient::class.java) }

    }

    protected fun randomString(): String {
        return UUID.randomUUID().toString().replace("-", "")
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