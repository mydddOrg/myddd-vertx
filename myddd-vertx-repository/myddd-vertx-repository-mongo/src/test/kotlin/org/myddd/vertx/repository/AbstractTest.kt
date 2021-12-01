package org.myddd.vertx.repository

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import java.util.*

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

    companion object {

        private val idGenerate by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext) {
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    vertx.executeBlocking<Void> {
                        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object :
                            AbstractModule() {
                            override fun configure() {
                                bind(Vertx::class.java).toInstance(vertx)
                                bind(MongoClient::class.java).toInstance(MongoClient.create(vertx, JsonObject()))
                                bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                                bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                                bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

                                bind(DocumentEntityRepository::class.java).to(DocumentEntityRepositoryMongo::class.java)
                            }
                        })))
                        it.complete()
                    }.await()

                } catch (t: Throwable) {
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }
    }

    protected fun randomString(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    protected fun randomMockDocumentEntity(): MockDocumentEntity {
        val random = MockDocumentEntity()
        random.name = UUID.randomUUID().toString()
        return random
    }
}