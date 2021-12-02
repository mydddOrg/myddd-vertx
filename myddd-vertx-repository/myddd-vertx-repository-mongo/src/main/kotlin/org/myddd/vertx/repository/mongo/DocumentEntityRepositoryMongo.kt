package org.myddd.vertx.repository.mongo

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.api.QueryOptions
import org.myddd.vertx.repository.mongo.ext.collectionName
import java.util.*
import kotlin.streams.toList

open class DocumentEntityRepositoryMongo:DocumentEntityRepository {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    val mongoClient: MongoClient by lazy { InstanceFactory.getInstance(MongoClient::class.java) }

    companion object {
        const val MONGO_ID = "_id"
    }

    override suspend fun <T : Entity> save(entity: T): Future<T> {
        val insertId = mongoClient.save(entity.collectionName(), JsonObject.mapFrom(entity)).await()
        if(Objects.nonNull(insertId)) entity.setId(insertId)
        return Future.succeededFuture(entity)
    }

    override suspend fun <T : Entity> batchInsert(entities: List<T>): Future<Unit> {
        require(entities.isNotEmpty()){"BATCH_ADD_ENTITIES_EMPTY"}
        val bulkOperations = entities.stream().map { BulkOperation.createInsert(JsonObject.mapFrom(it)) }.toList()
        mongoClient.bulkWrite(entities.stream().findAny().get().collectionName(),bulkOperations).await()

        return Future.succeededFuture(Unit)
    }

    override suspend fun <T : Entity> queryEntityById(clazz: Class<T>, id: String): Future<T?> {
        val query = mongoClient.findOne(clazz.collectionName(),JsonObject().put(MONGO_ID,id),null).await()
        return if(Objects.isNull(query)) Future.succeededFuture(null)
        else Future.succeededFuture(query.mapTo(clazz))
    }

    override suspend fun <T : Entity> singleQuery(clazz: Class<T>, query: JsonObject): Future<T?> {
        val findOneResult = mongoClient.findOne(clazz.collectionName(),query,null).await()
        return if(Objects.isNull(findOneResult)) Future.succeededFuture(null)
        else Future.succeededFuture(findOneResult.mapTo(clazz))
    }

    override suspend fun <T : Entity> removeEntity(clazz: Class<T>, id: String): Future<Unit> {
        mongoClient.findOneAndDelete(clazz.collectionName(),JsonObject().put(MONGO_ID,id)).await()
        return Future.succeededFuture(Unit)
    }

    override suspend fun <T : Entity> removeEntities(clazz: Class<T>, query: JsonObject): Future<Long> {
        val results = mongoClient.removeDocuments(clazz.collectionName(),query).await()
        return Future.succeededFuture(results.removedCount)
    }

    override suspend fun <T : Entity> listQuery(clazz: Class<T>, query: JsonObject): Future<List<T>> {
        val listSet = mongoClient.find(clazz.collectionName(),query).await()
        val entities = listSet.stream().map { it.mapTo(clazz) }.toList()
        return Future.succeededFuture(entities)
    }

    override suspend fun <T : Entity> listQueryWithOptions(clazz: Class<T>, query: JsonObject, options: QueryOptions): Future<List<JsonObject>> {
        val findOptions = FindOptions()
            .setFields(options.fields)
            .setSort(options.sort)
            .setLimit(options.limit)
            .setSkip(options.limit)
            .setBatchSize(options.batchSize)

        val query = mongoClient.findWithOptions(clazz.collectionName(),query,findOptions).await()
        return Future.succeededFuture(query)
    }
}