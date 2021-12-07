package org.myddd.vertx.media.qcloud

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider

class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object :
            AbstractModule() {
            override fun configure() {
                val vertx = Vertx.vertx()
                bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                bind(FileDigest::class.java).to(FileDigestProvider::class.java)

                bind(MediaStorage::class.java).toInstance(QCloudMediaStorage(
                    secretId = "AKIDXopZ5LR2pa5JHEJ4fz2EAuOcaHgrhkH3",
                    secretKey = "aNtFPKxIONPAez5uTlxTklZtymIOFrBD",
                    bucketName = "isv-gateway-test-1258930758"
                ))
            }
        })))
    }
}