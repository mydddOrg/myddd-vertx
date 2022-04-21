package org.myddd.vertx.media.storeage

import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.storage.LocalMediaExtra

class LocalMediaStorageTest:AbstractTest() {


    private val mediaStorage = LocalMediaStorage()

    @Test
    fun testConvert(testContext: VertxTestContext){
        testContext.execute {
            try {

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = mediaStorage.uploadToStorage(mediaFile).await()
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
    fun testDownloadMedia(testContext: VertxTestContext){
        testContext.execute {
            try {
                try {
                    mediaStorage.downloadFromStorage(extra = LocalMediaExtra(path = randomString())).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = mediaStorage.uploadToStorage(mediaFile).await()
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
    fun testUploadFileByCustomStoragePath(testContext: VertxTestContext){
        testContext.execute {
            try {
                val customMediaStorage = LocalMediaStorage(storagePath = "${System.getProperty("user.home")}/Downloads")

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path

                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = customMediaStorage.uploadToStorage(mediaFile).await()
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
    fun testUploadFile(testContext: VertxTestContext){
        testContext.execute {
            try {

                val absolutePath = LocalMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = mediaStorage.uploadToStorage(mediaFile).await()
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
    fun testRandomPath(testContext: VertxTestContext){
        testContext.execute {
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