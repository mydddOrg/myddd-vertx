package org.myddd.vertx.cache

import io.vertx.core.Future
import io.vertx.core.shareddata.Shareable

interface Cache<T:Shareable> {

    suspend fun set(key:String,value:T):Future<Unit>

    suspend fun get(key: String):Future<T?>

    suspend fun containsKey(key: String):Future<Boolean>

    suspend fun remove(key: String):Future<T?>

    suspend fun clear():Future<Unit>

}