package org.myddd.vertx.media.storage

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.MediaStorage

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }
        val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object :
                AbstractModule() {
                override fun configure() {
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                    bind(MediaStorage::class.java).to(GridFSMediaStorage::class.java)
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