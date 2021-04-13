package org.myddd.vertx.cache

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.lang.RuntimeException
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

class ShareDataCacheTest:AbstractTest() {

    private val errorCache:Cache<Entity> = Mockito.mock(Cache::class.java) as Cache<Entity>

    private fun successFuture(result:Any): Future<*> {
        val future = Mockito.mock(Future::class.java)
        Mockito.`when`(future.succeeded()).thenReturn(true)
        Mockito.`when`(future.failed()).thenReturn(false)
        Mockito.`when`(future.result()).thenReturn(result)
        return future
    }

    private fun failedFuture():Future<*>{
        val future = Mockito.mock(Future::class.java)
        Mockito.`when`(future.succeeded()).thenReturn(false)
        Mockito.`when`(future.failed()).thenReturn(true)
        Mockito.`when`(future.cause()).thenReturn(RuntimeException("出现错误了"))
        return future
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    companion object {

        private val localCache:Cache<Entity> = ShareDataCache(name = "Cache")
        private val disturbedCache:Cache<Entity> = ShareDataCache(name = "AsyncCache",localCache = false)
        private val anotherCache:Cache<Entity> = ShareDataCache(name = "AnotherCache")



        @JvmStatic
        fun parameterCache():Stream<Cache<Entity>>{
            return Stream.of(localCache,disturbedCache)
        }
    }

    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testContainsKey(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val key = UUID.randomUUID().toString()
                val entity = randomEntity()
                cache.set(key,entity).await()

                val contains = cache.containsKey(key).await()
                testContext.verify {
                    Assertions.assertTrue(contains)
                }

                val notContains = cache.containsKey(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertFalse(notContains)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testContainsKeyError(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val future = successFuture(true)
                Mockito.`when`(errorCache.containsKey(any())).thenReturn(future as Future<Boolean>)
                val contains = errorCache.containsKey(UUID.randomUUID().toString()).await()

                testContext.verify {
                    Assertions.assertTrue(contains)
                }

                val errorFuture = failedFuture()
                Mockito.`when`(errorCache.containsKey(any())).thenReturn(errorFuture as Future<Boolean>)
                try {
                    errorCache.containsKey(UUID.randomUUID().toString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testSetCache(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val key = UUID.randomUUID().toString()
                val entity = randomEntity()
                cache.set(key,entity).await()

                val getValue = cache.get(key).await()
                testContext.verify {
                    Assertions.assertNotNull(getValue)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSetCacheError(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val future = successFuture(Unit)
                Mockito.`when`(errorCache.set(any(),any())).thenReturn(future as Future<Unit>)

                val key = UUID.randomUUID().toString()
                val entity = randomEntity()
                errorCache.set(key, entity).await()


                val errorFuture = failedFuture()
                Mockito.`when`(errorCache.set(any(),any())).thenReturn(errorFuture as Future<Unit>)
                try {
                    errorCache.set(key, entity).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testGetCache(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val key = UUID.randomUUID().toString()


                val entity = randomEntity()
                cache.set(key,entity).await()

                val getValue = cache.get(key).await()
                testContext.verify {
                    Assertions.assertNotNull(getValue)
                }

                val notExists = cache.get(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(notExists)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGetCacheError(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val entity = randomEntity()


                val future = successFuture(entity)
                Mockito.`when`(errorCache.get(any())).thenReturn(future as Future<Entity?>)

                val key = UUID.randomUUID().toString()
                errorCache.get(key).await()

                val errorFuture = failedFuture()
                Mockito.`when`(errorCache.get(any())).thenReturn(errorFuture as Future<Entity?>)
                try {
                    errorCache.get(key).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testClearCache(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val key = UUID.randomUUID().toString()


                val entity = randomEntity()
                cache.set(key,entity).await()

                var getValue = cache.get(key).await()
                testContext.verify {
                    Assertions.assertNotNull(getValue)
                }

                cache.clear().await()
                getValue = cache.get(key).await()
                testContext.verify {
                    Assertions.assertNull(getValue)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testClearCacheError(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val future = successFuture(Unit)
                Mockito.`when`(errorCache.clear()).thenReturn(future as Future<Unit>)

                errorCache.clear().await()

                val errorFuture = failedFuture()
                Mockito.`when`(errorCache.clear()).thenReturn(errorFuture as Future<Unit>)
                try {
                    errorCache.clear().await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testRemoveCache(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val key = UUID.randomUUID().toString()

                val anotherKey = UUID.randomUUID().toString()

                val entity = randomEntity()
                cache.set(key,entity).await()
                cache.set(anotherKey,randomEntity()).await()

                var getValue = cache.get(key).await()
                testContext.verify {
                    Assertions.assertNotNull(getValue)
                }

                cache.remove(key).await()
                getValue = cache.get(key).await()

                val exist = cache.get(anotherKey).await()
                testContext.verify {
                    Assertions.assertNull(getValue)
                    Assertions.assertNotNull(exist)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRemoveCacheError(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val future = successFuture(randomEntity())
                Mockito.`when`(errorCache.remove(any())).thenReturn(future as Future<Entity?>)

                errorCache.remove(UUID.randomUUID().toString())

                val errorFuture = failedFuture()
                Mockito.`when`(errorCache.remove(any())).thenReturn(errorFuture as Future<Entity?>)
                try {
                    errorCache.remove(UUID.randomUUID().toString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @ParameterizedTest
    @MethodSource("parameterCache")
    fun testNotSomeCache(cache:Cache<Entity>,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                testContext.verify {
                    Assertions.assertNotEquals(cache,anotherCache)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomEntity():Entity{
        val entity = Entity()
        entity.name = UUID.randomUUID().toString()
        entity.age = Random.nextInt()
        return entity
    }
}