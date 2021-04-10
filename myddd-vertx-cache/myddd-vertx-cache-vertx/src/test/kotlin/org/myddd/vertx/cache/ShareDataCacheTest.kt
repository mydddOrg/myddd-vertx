package org.myddd.vertx.cache

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import kotlin.random.Random

class ShareDataCacheTest:AbstractTest() {

    private val cache:Cache<Entity> by lazy { InstanceFactory.getInstance(Cache::class.java,"Cache") as Cache<Entity> }

    private val anotherCache:Cache<Entity> by lazy { InstanceFactory.getInstance(Cache::class.java,"AnotherCache") as Cache<Entity> }

    @Test
    fun testNotSomeCache(vertx: Vertx,testContext: VertxTestContext){
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

    @Test
    fun testSetCache(vertx: Vertx,testContext: VertxTestContext){
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
    fun testGetCache(vertx: Vertx,testContext: VertxTestContext){
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
    fun testClearCache(vertx: Vertx,testContext: VertxTestContext){
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
    fun testRemoveCache(vertx: Vertx,testContext: VertxTestContext){
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
    private fun randomEntity():Entity{
        val entity = Entity()
        entity.name = UUID.randomUUID().toString()
        entity.age = Random.nextInt()
        return entity
    }
}