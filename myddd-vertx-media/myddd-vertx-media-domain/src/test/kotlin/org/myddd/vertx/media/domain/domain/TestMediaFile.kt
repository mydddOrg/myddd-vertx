package org.myddd.vertx.media.domain.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.AbstractTest
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryHibernate
import org.myddd.vertx.media.storeage.LocalMediaStorage
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
class TestMediaFile {

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

                    bind(MediaStorage::class.java).toInstance(LocalMediaStorage())
                }
            })))

            testContext.completeNow()
        }
    }

    private fun randomString():String {
        return randomIDString.randomString()
    }

    @Test
    fun testMediaFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    MediaFile.of(randomString()).await()
                    testContext.failNow("应该抛出异常，不会执行到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val path = TestMediaFile::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaFile = MediaFile.of(path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaFile)
                }

                val buffer = mediaFile.toBuffer()
                testContext.verify {
                    Assertions.assertNotNull(buffer)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}