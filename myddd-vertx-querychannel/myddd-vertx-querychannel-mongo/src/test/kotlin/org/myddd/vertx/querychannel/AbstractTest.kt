package org.myddd.vertx.querychannel

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
import org.myddd.vertx.querychannel.api.DocumentQueryChannel
import org.myddd.vertx.querychannel.mock.MockUser
import org.myddd.vertx.querychannel.mongo.DocumentQueryChannelMongo
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import java.util.*

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

    companion object {

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
                    bind(DocumentQueryChannel::class.java).to(DocumentQueryChannelMongo::class.java)
                }
            }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext) {
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.completeNow()
        }
    }

    protected fun randomString(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    protected fun randomMockUser(): MockUser {
        val user = MockUser()
        user.name = randomString()
        return user
    }
}