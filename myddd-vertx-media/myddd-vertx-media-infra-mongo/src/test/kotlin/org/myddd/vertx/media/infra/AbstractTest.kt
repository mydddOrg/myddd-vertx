package org.myddd.vertx.media.infra

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                                bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                                bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                                bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                                bind(MediaStorage::class.java).toInstance(LocalMediaStorage())

                                bind(MediaRepository::class.java).to(MediaRepositoryMongo::class.java)
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