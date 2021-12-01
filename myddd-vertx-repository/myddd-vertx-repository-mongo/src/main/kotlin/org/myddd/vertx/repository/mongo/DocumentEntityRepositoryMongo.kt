package org.myddd.vertx.repository.mongo

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.DocumentEntity
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.ext.collectionName
import java.util.*
import kotlin.streams.toList

open class DocumentEntityRepositoryMongo:DocumentEntityRepository {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    val mongoClient: MongoClient by lazy { MongoClient.create(vertx, JsonObject()) }

    companion object {
        private const val MONGO_ID = "_id"
    }

    override suspend fun <T : Entity> insert(entity: T): Future<T> {
        val insertId = mongoClient.save(entity.collectionName(), JsonObject.mapFrom(entity)).await()
        entity.setId(insertId)
        return Future.succeededFuture(entity)
    }

    override suspend fun <T : Entity> queryEntityById(id: String, clazz: Class<T>): Future<T?> {
        val query = mongoClient.findOne(clazz.collectionName(),JsonObject().put(MONGO_ID,id),null).await()
        return if(Objects.isNull(query)) Future.succeededFuture(null)
        else Future.succeededFuture(query.mapTo(clazz))
    }

    override suspend fun <T : Entity> singleQuery(query: JsonObject, clazz: Class<T>): Future<T?> {
        val findOneResult = mongoClient.findOne(clazz.collectionName(),query,null).await()
        return if(Objects.isNull(findOneResult)) Future.succeededFuture(null)
        else Future.succeededFuture(findOneResult.mapTo(clazz))
    }

    override suspend fun <T : Entity> removeEntity(id: String, clazz: Class<T>): Future<Unit> {
        mongoClient.findOneAndDelete(clazz.collectionName(),JsonObject().put(MONGO_ID,id)).await()
        return Future.succeededFuture(Unit)
    }

    override suspend fun <T : Entity> listQuery(query: JsonObject, clazz: Class<T>): Future<List<T>> {
        val listSet = mongoClient.find(clazz.collectionName(),query).await()
        val entities = listSet.stream().map { it.mapTo(clazz) }.toList()
        return Future.succeededFuture(entities)
    }
}