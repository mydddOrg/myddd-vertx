package org.myddd.vertx.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await

object JsonMapper {

    suspend fun <T> mapFrom(vertx: Vertx, jsonString:String, clazz: Class<T>): Future<T> {
        return try {
            val mapper = ObjectMapper().registerModule(KotlinModule())
            val value:T = vertx.executeBlocking<T> {
                it.complete(mapper.readValue(jsonString, clazz))
            }.await()
            Future.succeededFuture(value)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}