package org.myddd.vertx.repository.mongo

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.IndexOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.Document
import org.myddd.vertx.domain.ext.collectionName
import org.myddd.vertx.domain.ext.indexes
import org.myddd.vertx.domain.ext.uniqueConstraints
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.api.QueryOptions
import java.util.*

open class DocumentEntityRepositoryMongo:DocumentEntityRepository {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    val mongoClient: MongoClient by lazy { InstanceFactory.getInstance(MongoClient::class.java) }

    companion object {
        const val MONGO_ID = "_id"
    }

    override suspend fun <T : Document> save(entity: T): Future<T> {
        val insertId = mongoClient.save(entity.collectionName(), JsonObject.mapFrom(entity)).await()
        if(Objects.nonNull(insertId)) entity.id = insertId
        return Future.succeededFuture(entity)
    }

    override suspend fun <T : Document> batchInsert(entities: List<T>): Future<Unit> {
        require(entities.isNotEmpty()){"BATCH_ADD_ENTITIES_EMPTY"}
        val bulkOperations = entities.stream().map { BulkOperation.createInsert(JsonObject.mapFrom(it)) }.toList()
        mongoClient.bulkWrite(entities.stream().findAny().get().collectionName(),bulkOperations).await()

        return Future.succeededFuture(Unit)
    }

    override suspend fun <T : Document> get(clazz: Class<T>, id: String): Future<T?> {
        val query = mongoClient.findOne(clazz.collectionName(),JsonObject().put(MONGO_ID,id),null).await()
        return if(Objects.isNull(query)) Future.succeededFuture(null)
        else Future.succeededFuture(query.mapTo(clazz))
    }

    override suspend fun <T : Document> singleQuery(clazz: Class<T>, query: JsonObject): Future<T?> {
        val findOneResult = mongoClient.findOne(clazz.collectionName(),query,null).await()
        return if(Objects.isNull(findOneResult)) Future.succeededFuture(null)
        else Future.succeededFuture(findOneResult.mapTo(clazz))
    }

    override suspend fun <T : Document> removeEntity(clazz: Class<T>, id: String): Future<Unit> {
        mongoClient.findOneAndDelete(clazz.collectionName(),JsonObject().put(MONGO_ID,id)).await()
        return Future.succeededFuture(Unit)
    }

    override suspend fun <T : Document> removeEntities(clazz: Class<T>, query: JsonObject): Future<Long> {
        val results = mongoClient.removeDocuments(clazz.collectionName(),query).await()
        return Future.succeededFuture(results.removedCount)
    }

    override suspend fun <T : Document> listQuery(clazz: Class<T>, query: JsonObject): Future<List<T>> {
        val listSet = mongoClient.find(clazz.collectionName(),query).await()
        val entities = listSet.stream().map { it.mapTo(clazz) }.toList()
        return Future.succeededFuture(entities)
    }

    override suspend fun <T : Document> listQueryWithOptions(clazz: Class<T>, query: JsonObject, options: QueryOptions): Future<List<JsonObject>> {
        val findOptions = FindOptions()
            .setFields(options.fields)
            .setSort(options.sort)
            .setLimit(options.limit)
            .setSkip(options.limit)
            .setBatchSize(options.batchSize)

        return Future.succeededFuture(mongoClient.findWithOptions(clazz.collectionName(),query,findOptions).await())
    }

    suspend fun <T:Document> createDocument(clazz: Class<T>):Future<Unit>{
        createDocumentUniqueConstraints(clazz).await()
        createDocumentIndexes(clazz).await()
        return Future.succeededFuture(Unit)
    }

    private suspend fun <T:Document> createDocumentIndexes(clazz: Class<T>):Future<Unit>{
        val indexes = clazz.indexes()
        val exists = mongoClient.listIndexes(clazz.collectionName()).await()
        val existsIndexes = exists.stream().map { (it as JsonObject).getJsonObject("key").fieldNames().toString() }.toList()
        indexes.forEach { index ->
            val indexJsonObject = JsonObject().put(index.columnList,1)
            if(!existsIndexes.contains(indexJsonObject.fieldNames().toString())){
                mongoClient.createIndex(clazz.collectionName(),indexJsonObject).await()
            }
        }
        return Future.succeededFuture()
    }

   private suspend fun <T:Document> createDocumentUniqueConstraints(clazz: Class<T>):Future<Unit>{
        val uniqueConstraints = clazz.uniqueConstraints()

        val exists = mongoClient.listIndexes(clazz.collectionName()).await()
        val existsIndexes = exists.stream().map { (it as JsonObject).getJsonObject("key").fieldNames().toString() }.toList()

        uniqueConstraints.forEach { uniqueConstraint ->
            val jsonObject = JsonObject()
            uniqueConstraint.columnNames.asList().forEach { name ->
                jsonObject.put(name,1)
            }
            if(!existsIndexes.contains(jsonObject.fieldNames().toString())){
                mongoClient.createIndexWithOptions(clazz.collectionName(),jsonObject,IndexOptions().unique(true)).await()
            }
        }
        return Future.succeededFuture()
    }
}