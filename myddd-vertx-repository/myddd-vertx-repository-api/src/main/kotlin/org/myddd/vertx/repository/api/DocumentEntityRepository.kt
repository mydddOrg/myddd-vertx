package org.myddd.vertx.repository.api

import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import org.myddd.vertx.domain.Document
import org.myddd.vertx.domain.Entity
import javax.persistence.Index
import javax.persistence.UniqueConstraint

interface DocumentEntityRepository {

    suspend fun <T:Document> save(entity:T):Future<T>

    suspend fun <T:Document> batchInsert(entities:List<T>):Future<Unit>

    suspend fun <T:Document> get(clazz: Class<T>, id: String):Future<T?>

    suspend fun <T:Document> singleQuery(clazz: Class<T>, query: JsonObject):Future<T?>

    suspend fun <T:Document> removeEntity(clazz: Class<T>, id: String):Future<Unit>

    suspend fun <T:Document> removeEntities(clazz: Class<T>, query: JsonObject = JsonObject()):Future<Long>

    suspend fun <T:Document> listQuery(clazz: Class<T>, query: JsonObject):Future<List<T>>

    suspend fun <T:Document> listQueryWithOptions(clazz: Class<T>, query: JsonObject, options: QueryOptions):Future<List<JsonObject>>


}