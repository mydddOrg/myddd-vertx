package org.myddd.vertx.media.qcloud

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaStorage

class QCloudMediaStorageTest:AbstractTest() {


    private val mediaStorage by lazy { InstanceFactory.getInstance(MediaStorage::class.java) }

    @Test
    fun testKeyForFilePath(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val qCloudMediaStorage = mediaStorage as QCloudMediaStorage
                try {
                    qCloudMediaStorage.keyForFilePath(randomIDString.randomUUID())
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
    fun testUploadFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    mediaStorage.uploadToStorage(randomIDString.randomString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = QCloudMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = mediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug(extra.destPath())
                    Assertions.assertNotNull(extra)
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
                    mediaStorage.downloadFromStorage(extra = QCloudMediaExtra(key = randomIDString.randomString())).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val absolutePath = QCloudMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val extra = mediaStorage.uploadToStorage(absolutePath).await()
                testContext.verify {
                    logger.debug((extra as QCloudMediaExtra).destPath())
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