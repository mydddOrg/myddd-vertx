package com.foreverht.isvgateway.workplus

import com.foreverht.isvgateway.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.MediaApplication
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.io.File
import java.util.*

class MediaApplicationWorkPlusTest : AbstractWorkPlusTest() {

    companion object {
        private val logger = LoggerFactory.getLogger(MediaApplicationWorkPlusTest::class.java)
        private val mediaApplication:MediaApplication by lazy { InstanceFactory.getInstance(MediaApplication::class.java,"WorkPlusApp") }
        private const val mediaId = "ad6a568cfbb540f0ad75d10e77d233de"
    }

    @Test
    fun testDownloadFile(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val mediaDTO = mediaApplication.downloadFile(clientId = isvClientId, mediaId = mediaId).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaDTO)
                    Assertions.assertEquals(648421,mediaDTO.size)
                }

                try {
                    mediaApplication.downloadFile(clientId = isvClientId, mediaId = UUID.randomUUID().toString()).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                     mediaApplication.downloadFile(clientId = UUID.randomUUID().toString(), mediaId = mediaId).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
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
                val path = "META-INF/my_avatar.png"
                var absolutePath = MediaApplicationWorkPlusTest::class.java.classLoader.getResource(path)!!.path
                val mediaId = mediaApplication.uploadFile(clientId = isvClientId,path = absolutePath).await()
                logger.debug("mediaId:$mediaId")
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGetFileProps(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val fs = vertx.fileSystem()
                val path = "META-INF/my_avatar.png"
                val name = path.substring(path.lastIndexOf(File.separator) + 1)
                val fileSystemProps = fs.lprops("META-INF/my_avatar.png").await()
                testContext.verify { Assertions.assertNotNull(fileSystemProps) }

                val md5 = vertx.executeBlocking<String> {
                    logger.debug(MediaApplicationWorkPlusTest::class.java.classLoader.getResource(path)!!.path)
                    val md5 = DigestUtils.md5Hex(MediaApplicationWorkPlusTest::class.java.classLoader.getResourceAsStream(path))
                    it.complete(md5)
                }.await()

                logger.debug("name:$name,size:${fileSystemProps.size()},md5:$md5")

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}