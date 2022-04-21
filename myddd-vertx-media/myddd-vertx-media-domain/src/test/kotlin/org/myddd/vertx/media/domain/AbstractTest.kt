package org.myddd.vertx.media.domain

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
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
import org.myddd.vertx.media.infra.repository.MediaRepositoryHibernate
import org.myddd.vertx.media.storeage.LocalMediaStorage
import org.myddd.vertx.repository.hibernate.MydddServiceContributor
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    MydddServiceContributor.vertx = vertx

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
            }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.completeNow()
        }
    }
}