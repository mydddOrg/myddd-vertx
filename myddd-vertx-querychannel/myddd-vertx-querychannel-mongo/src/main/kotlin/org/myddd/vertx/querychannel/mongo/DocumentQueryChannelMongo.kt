package org.myddd.vertx.querychannel.mongo

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.Document
import org.myddd.vertx.domain.Entity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.DocumentQueryChannel
import org.myddd.vertx.querychannel.api.Page
import org.myddd.vertx.domain.ext.collectionName
import kotlin.streams.toList

class DocumentQueryChannelMongo:DocumentQueryChannel {

    private val mongoClient by lazy { InstanceFactory.getInstance(MongoClient::class.java) }

    override suspend fun <T : Document> pageQuery(clazz: Class<T>, query: JsonObject, limit: Int, skip: Int): Future<Page<T>> {
        val findOptions = FindOptions().setSkip(skip).setLimit(limit)
        val dataList = mongoClient.findWithOptions(clazz.collectionName(),query,findOptions).await()
        val totalCount = mongoClient.count(clazz.collectionName(),query).await()
        val entities = dataList.stream().map { it.mapTo(clazz) }.toList()
        return Future.succeededFuture(Page(dataList = entities,totalCount = totalCount,skip = skip,limit = limit))
    }

    override suspend fun <T : Document> listQuery(clazz: Class<T>, query: JsonObject, limit: Int, skip: Int): Future<List<T>> {
        val findOptions = FindOptions().setSkip(skip).setLimit(limit)
        val dataList = mongoClient.findWithOptions(clazz.collectionName(),query,findOptions).await()
        val entities = dataList.stream().map { it.mapTo(clazz) }.toList()
        return Future.succeededFuture(entities)
    }
}