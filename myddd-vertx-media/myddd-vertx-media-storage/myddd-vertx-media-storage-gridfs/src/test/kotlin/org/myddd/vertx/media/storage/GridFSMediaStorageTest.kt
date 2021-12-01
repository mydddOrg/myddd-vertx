package org.myddd.vertx.media.storage

import io.vertx.core.Vertx
import io.vertx.ext.mongo.MongoClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaStorage

@Disabled
class GridFSMediaStorageTest : AbstractTest() {


    private val mediaStorage by lazy {
        InstanceFactory.getInstance(MediaStorage::class.java)
    }

    @Test
    fun testCreateMongodbClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val config = json {
                    obj(
                        "connection_string" to "mongodb://172.16.1.248:27017/I7e"
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
    fun testUploadFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
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
    fun testDownloadFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val absolutePath = GridFSMediaStorageTest::class.java.classLoader.getResource("my_avatar.png")!!.path
                val mediaFile = MediaFile.of(absolutePath).await()

                val fs = vertx.fileSystem()
                val destFile = fs.createTempFile("gridFs",".png").await()
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