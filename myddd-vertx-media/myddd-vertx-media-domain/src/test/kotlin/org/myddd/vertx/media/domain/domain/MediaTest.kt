package org.myddd.vertx.media.domain.domain

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
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.media.MediaNotFoundException
import org.myddd.vertx.media.domain.AbstractTest
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryHibernate
import org.myddd.vertx.media.storeage.LocalMediaStorage
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
class MediaTest {

    companion object {

        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

        val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(vertx)

                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                    bind(MediaRepository::class.java).to(MediaRepositoryHibernate::class.java)
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                    bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)

                    bind(MediaStorage::class.java).toInstance(LocalMediaStorage())
                }
            })))

            testContext.completeNow()
        }
    }

    @Test
    fun testQueryByDigest(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExistMedia = Media.queryMediaByDigest(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.getId())
                }

                val query = Media.queryMediaByDigest(digest = created.digest).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryByMediaId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExistMedia = Media.queryMediaById(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.getId())
                }

                val query = Media.queryMediaById(mediaId = created.id).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testDownloadMedia(testContext: VertxTestContext){
        testContext.execute {
            testContext.assertThrow(MediaNotFoundException::class.java){
                Media.downloadByMediaId(randomString()).await()
            }

            val created = createMedia().await()

            val downloadPath = Media.downloadByMediaId(created.id).await()
            testContext.verify {
                Assertions.assertNotNull(downloadPath)
            }
        }
    }

    private fun randomString():String {
        return TestMediaFile.randomIDString.randomString()
    }

    @Test
    fun testCreateMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun createMedia():Future<Media>{
        return try {
            val path = MediaTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
            val media = Media.createByLocalFile(path = path).await()
            Future.succeededFuture(media)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}