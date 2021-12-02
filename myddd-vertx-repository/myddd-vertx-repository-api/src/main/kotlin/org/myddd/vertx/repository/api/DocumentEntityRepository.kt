package org.myddd.vertx.repository.api

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.myddd.vertx.domain.Entity

interface DocumentEntityRepository {

    suspend fun <T:Entity> save(entity:T):Future<T>

    suspend fun <T:Entity> batchInsert(entities:List<T>):Future<Unit>

    suspend fun <T:Entity> queryEntityById(clazz: Class<T>, id: String):Future<T?>

    suspend fun <T:Entity> singleQuery(clazz: Class<T>, query: JsonObject):Future<T?>

    suspend fun <T:Entity> removeEntity(clazz: Class<T>, id: String):Future<Unit>

    suspend fun <T:Entity> removeEntities(clazz: Class<T>, query: JsonObject):Future<Long>

    suspend fun <T:Entity> listQuery(clazz: Class<T>, query: JsonObject):Future<List<T>>

    suspend fun <T:Entity> listQueryWithOptions(clazz: Class<T>, query: JsonObject, options: QueryOptions):Future<List<JsonObject>>

}