package org.myddd.vertx.media.infra

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryMongo
import org.myddd.vertx.media.storeage.LocalMediaStorage
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider

class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object :
            AbstractModule() {
            override fun configure() {
                val vertx = Vertx.vertx()
                bind(Vertx::class.java).toInstance(vertx)
                bind(MongoClient::class.java).toInstance(MongoClient.create(vertx, JsonObject()))
                bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                bind(FileDigest::class.java).to(FileDigestProvider::class.java)
                bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                bind(MediaStorage::class.java).toInstance(LocalMediaStorage())

                bind(MediaRepository::class.java).to(MediaRepositoryMongo::class.java)
            }
        })))
    }
}