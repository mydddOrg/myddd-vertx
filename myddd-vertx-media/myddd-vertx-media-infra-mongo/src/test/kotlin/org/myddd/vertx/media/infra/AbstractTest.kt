package org.myddd.vertx.media.infra

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryMongo
import org.myddd.vertx.media.storeage.LocalMediaStorage
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

    companion object {

        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule() {
                override fun configure() {
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    bind(MongoClient::class.java).toInstance(MongoClient.create(vertx, JsonObject()))
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                    bind(MediaStorage::class.java).toInstance(LocalMediaStorage())
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                    bind(MediaRepository::class.java).to(MediaRepositoryMongo::class.java)
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


    protected suspend fun createMedia(): Future<Media> {
        return try {
            val path = AbstractTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
            val media = Media.createByLocalFile(path = path).await()
            Future.succeededFuture(media)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}