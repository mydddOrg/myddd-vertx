package org.myddd.vertx.media.qcloud

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.storage.QCloudMediaExtra

@Disabled
class QCloudMediaStorageTest:AbstractTest() {


    private val mediaStorage by lazy { InstanceFactory.getInstance(MediaStorage::class.java) }

    @Test
    fun testKeyForFilePath(testContext: VertxTestContext){
        testContext.execute{
            try {
                val qCloudMediaStorage = mediaStorage as QCloudMediaStorage
                try {
                    qCloudMediaStorage.keyForFilePath(randomString())
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val localFile = QCloudTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val fileKey = qCloudMediaStorage.keyForFilePath(localFile)
                testContext.verify {
                    Assertions.assertNotNull(fileKey)
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

                val absolutePath = QCloudMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = mediaStorage.uploadToStorage(mediaFile).await()
                testContext.verify {
                    Assertions.assertNotNull(extra)
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
                    mediaStorage.downloadFromStorage(extra = QCloudMediaExtra(key = randomString())).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = QCloudMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()
                val extra = mediaStorage.uploadToStorage(mediaFile).await()
                testContext.verify {
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
}