package org.myddd.vertx.querychannel.api

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.myddd.vertx.domain.Document

interface DocumentQueryChannel {
    suspend fun <T:Document> pageQuery(clazz: Class<T>, query:JsonObject = JsonObject(), limit:Int = 100, skip:Int = 0):Future<Page<T>>

    suspend fun <T:Document> listQuery(clazz: Class<T>,query:JsonObject = JsonObject(),limit:Int = 100,skip:Int = 0): Future<List<T>>
}