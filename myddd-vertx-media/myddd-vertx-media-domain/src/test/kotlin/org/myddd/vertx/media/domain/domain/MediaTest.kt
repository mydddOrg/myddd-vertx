package org.myddd.vertx.media.domain.domain

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.media.domain.AbstractTest
import org.myddd.vertx.media.domain.Media


class MediaTest: AbstractTest() {

    @Test
    fun testQueryByDigest(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val notExistMedia = Media.queryMediaByDigest(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }

                val query = Media.queryMediaByDigest(digest = created.digest).await()
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
                val notExistMedia = Media.queryMediaById(randomString()).await()
                testContext.verify { Assertions.assertNull(notExistMedia) }

                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertTrue(created.getId() > 0)
                }

                val query = Media.queryMediaById(mediaId = created.mediaId).await()
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
    fun testCreateMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = createMedia().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.extra.destPath())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun createMedia():Future<Media>{
        return try {
            val path = MediaTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
            val media = Media.createByLocalFile(path = path).await()
            Future.succeededFuture(media)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}