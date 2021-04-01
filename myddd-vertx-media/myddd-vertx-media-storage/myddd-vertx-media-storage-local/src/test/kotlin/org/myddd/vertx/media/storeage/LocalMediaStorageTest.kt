package org.myddd.vertx.media.storeage

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LocalMediaStorageTest:AbstractTest() {


    private val mediaStorage = LocalMediaStorage()

    @Test
    fun testConvert(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = mediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug((extra as LocalMediaExtra).path)
                    Assertions.assertNotNull(extra)
                }

                val entity = mediaStorage.loadMediaExtra(JsonObject.mapFrom(extra).toString())
                testContext.verify {
                    Assertions.assertNotNull(entity)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testDownloadMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    mediaStorage.downloadFromStorage(extra = LocalMediaExtra(path = randomIDString.randomString())).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = mediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug((extra as LocalMediaExtra).path)
                    Assertions.assertNotNull(extra)
                }

                val downloadPath = mediaStorage.downloadFromStorage(extra).await()
                testContext.verify {
                    Assertions.assertNotNull(downloadPath)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUploadFileByCustomStoragePath(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val customMediaStorage = LocalMediaStorage(storagePath = "${System.getProperty("user.home")}/Downloads")
                try {
                    customMediaStorage.uploadToStorage(randomIDString.randomString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = customMediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug((extra as LocalMediaExtra).path)
                    Assertions.assertNotNull(extra)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUploadFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    mediaStorage.uploadToStorage(randomIDString.randomString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = mediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug((extra as LocalMediaExtra).path)
                    Assertions.assertNotNull(extra)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRandomPath(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val randomPath = mediaStorage.randomFilePath().await()
                testContext.verify {
                    Assertions.assertNotNull(randomPath)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }



}