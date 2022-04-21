package org.myddd.vertx.media.qcloud

import com.google.inject.AbstractModule
import com.google.inject.Guice
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
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object :
                AbstractModule() {
                override fun configure() {
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(FileDigest::class.java).to(FileDigestProvider::class.java)

                    bind(MediaStorage::class.java).toInstance(QCloudMediaStorage(
                        secretId = "AKIDXopZ5LR2pa5JHEJ4fz2EAuOcaHgrhkH3",
                        secretKey = "aNtFPKxIONPAez5uTlxTklZtymIOFrBD",
                        bucketName = "isv-gateway-test-1258930758"
                    ))
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