package org.myddd.vertx.cache

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.shareddata.Shareable
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class ShareDataCache<T:Shareable>(private val name:String,private val localCache:Boolean = true):Cache<T> {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    override suspend fun set(key: String, value: T): Future<Unit> {
        return try {
            val cache = getAsyncMap().await()
            cache.put(key,value)
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