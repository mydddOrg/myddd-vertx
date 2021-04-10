package org.myddd.vertx.cache

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.shareddata.Shareable
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import java.io.Serializable
import java.util.*

class ShareDataCache<T>(private val name:String,private val localCache:Boolean = true,private val ttl:Long = 1000 * 60 * 5):Cache<T> where T:Shareable,T: Serializable {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    override suspend fun set(key: String, value: T): Future<Unit> {
        return try {
            val cache = getAsyncMap().await()
            cache.put(key,value,ttl)
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun get(key: String): Future<T?> {
        return try {
            val cache = getAsyncMap().await()
            cache.get(key)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun containsKey(key: String): Future<Boolean> {
        return try {
            val cache = getAsyncMap().await()
            val entity = cache.get(key).await()
            if(Objects.isNull(entity))Future.succeededFuture(false) else Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun clear(): Future<Unit> {
        return try {
            val cache = getAsyncMap().await()
            cache.clear()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun remove(key: String): Future<T?> {
        return try {
            val cache = getAsyncMap().await()
            cache.remove(key)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private fun getAsyncMap():Future<AsyncMap<String,T>>{
        return try {
            if(localCache){
                vertx.sharedData().getAsyncMap(name)
            }else{
                vertx.sharedData().getLocalAsyncMap(name)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }




}