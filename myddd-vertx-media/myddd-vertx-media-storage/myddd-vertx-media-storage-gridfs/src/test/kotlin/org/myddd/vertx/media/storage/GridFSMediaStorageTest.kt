package org.myddd.vertx.media.storage

import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaStorage

@Disabled
class GridFSMediaStorageTest : AbstractTest() {


    private val mediaStorage by lazy {
        InstanceFactory.getInstance(MediaStorage::class.java)
    }

    @Test
    fun testCreateMongodbClient(testContext: VertxTestContext){
        testContext.execute {
            try {
                val config = json {
                    obj(
                        "connection_string" to "mongodb://127.0.0.1:27017/test"
                    )
                }
                val client = MongoClient.create(vertx,config)
                testContext.verify {
                    Assertions.assertNotNull(client)
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
                val absolutePath = GridFSMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()

                val extra = mediaStorage.uploadToStorage(mediaFile).await()
                testContext.verify {
                    Assertions.assertNotNull(extra)
                }
                val gridFSMediaExtra = extra as GridFSMediaExtra
                logger.debug(gridFSMediaExtra.fileId)
            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testDownloadFile(testContext: VertxTestContext){
        testContext.execute {
            try {
                val absolutePath = GridFSMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()

                val extra = mediaStorage.uploadToStorage(mediaFile).await()
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