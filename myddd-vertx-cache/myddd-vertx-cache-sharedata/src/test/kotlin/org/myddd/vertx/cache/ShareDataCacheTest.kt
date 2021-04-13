package org.myddd.vertx.cache

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

class ShareDataCacheTest:AbstractTest() {



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