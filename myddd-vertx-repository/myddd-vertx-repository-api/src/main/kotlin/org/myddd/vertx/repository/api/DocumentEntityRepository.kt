package org.myddd.vertx.repository.api

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.myddd.vertx.domain.Entity

interface DocumentEntityRepository {

    suspend fun <T:Entity> save(entity:T):Future<T>

    suspend fun <T:Entity> batchInsert(entities:List<T>):Future<Unit>

    suspend fun <T:Entity> queryEntityById(id:String, clazz: Class<T>):Future<T?>

    suspend fun <T:Entity> singleQuery(query:JsonObject,clazz: Class<T>):Future<T?>

    suspend fun <T:Entity> removeEntity(id:String,clazz: Class<T>):Future<Unit>

    suspend fun <T:Entity> removeEntities(query: JsonObject,clazz: Class<T>):Future<Long>

    suspend fun <T:Entity> listQuery(query:JsonObject,clazz: Class<T>):Future<List<T>>

    suspend fun <T:Entity> listQueryWithOptions(query: JsonObject,options: QueryOptions,clazz: Class<T>):Future<List<JsonObject>>

}