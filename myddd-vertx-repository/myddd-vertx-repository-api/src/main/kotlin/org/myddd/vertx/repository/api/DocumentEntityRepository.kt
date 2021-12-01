package org.myddd.vertx.repository.api

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.myddd.vertx.domain.DocumentEntity

interface DocumentEntityRepository {

    suspend fun <T:DocumentEntity> insert(entity:T):Future<T>

    suspend fun <T:DocumentEntity> queryEntityById(id:String, clazz: Class<T>):Future<T?>

    suspend fun <T:DocumentEntity> singleQuery(query:JsonObject,clazz: Class<T>):Future<T?>

    suspend fun <T:DocumentEntity> removeEntity(id:String,clazz: Class<T>):Future<Unit>

    suspend fun <T:DocumentEntity> listQuery(query:JsonObject,clazz: Class<T>):Future<List<T>>

}