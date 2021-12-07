package org.myddd.vertx.repository

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.id.ULIDStringGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo
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
                bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                bind(StringIDGenerator::class.java).to(ULIDStringGenerator::class.java)
                bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

                bind(DocumentEntityRepository::class.java).to(DocumentEntityRepositoryMongo::class.java)
            }
        })))
    }
}