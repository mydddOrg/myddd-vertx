package org.myddd.vertx.media.storeage

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

class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object :
            AbstractModule() {
            override fun configure() {
                val vertx = Vertx.vertx()
                bind(Vertx::class.java).toInstance(vertx)
                bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                bind(MediaStorage::class.java).to(LocalMediaStorage::class.java)
            }
        })))
    }
}