package org.myddd.vertx.repository.mongo

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.DocumentEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.DocumentEntityRepository
import java.util.*

class DocumentEntityRepositoryMongo:DocumentEntityRepository {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val mongoClient by lazy { MongoClient.create(vertx, JsonObject()) }

    companion object {
        private const val MONGO_ID = "_id"
    }

    override suspend fun <T : DocumentEntity> insert(entity: T): Future<T?> {
        val insertId = mongoClient.insert(entity::class.java.simpleName, JsonObject.mapFrom(entity)).await()
        entity.id = insertId
        return Future.succeededFuture(entity)
    }

    override suspend fun <T : DocumentEntity> queryById(id: String,clazz: Class<T>): Future<T?> {
        val query = mongoClient.findOne(clazz.simpleName,JsonObject().put(MONGO_ID,id),null).await()
        return if(Objects.isNull(query)) Future.succeededFuture(null)
        else Future.succeededFuture(query.mapTo(clazz))
    }
}