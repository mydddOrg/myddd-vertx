package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class ProxyMediaTest:AbstractTest() {

    @Test
    fun testQueryByDigest(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExistMedia = ProxyMedia.queryMediaByDigest(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val randomProxyMedia = randomProxyMedia()
                val created = randomProxyMedia.createProxyMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }

                val query = ProxyMedia.queryMediaByDigest(digest = created.digest).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryByMediaId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExistMedia = ProxyMedia.queryMediaById(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val randomProxyMedia = randomProxyMedia()
                val created = randomProxyMedia.createProxyMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }

                val query = ProxyMedia.queryMediaById(mediaId = created.mediaId).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateProxyMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomProxyMedia = randomProxyMedia()
                val created = randomProxyMedia.createProxyMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }

                try {
                    val errorMedia = randomProxyMedia()
                    errorMedia.relateId = randomIDString.randomString(256)

                    errorMedia.createProxyMedia().await()
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


    private fun randomProxyMedia():ProxyMedia{
        val proxyMedia = ProxyMedia()
        proxyMedia.relateId = randomString()
        proxyMedia.digest = randomString()
        proxyMedia.name = randomString()
        return proxyMedia
    }
}