package org.myddd.vertx.repository.api

import io.vertx.core.Future
import org.myddd.vertx.domain.DocumentEntity

interface DocumentEntityRepository {

    suspend fun <T:DocumentEntity> insert(entity:T):Future<T?>

    suspend fun <T:DocumentEntity> queryById(id:String,clazz: Class<T>):Future<T?>

}