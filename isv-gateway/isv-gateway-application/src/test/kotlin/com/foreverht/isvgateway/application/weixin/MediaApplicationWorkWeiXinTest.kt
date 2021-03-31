package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MediaApplication
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class MediaApplicationWorkWeiXinTest:AbstractWorkWeiXinTest() {

    private val mediaApplication by lazy { InstanceFactory.getInstance(MediaApplication::class.java, WORK_WEI_XIN) }

    @Test
    fun testDownloadMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val path = MediaApplicationWorkWeiXinTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaId = mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }

                val mediaDTO = mediaApplication.downloadFile(isvAccessToken,mediaId).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaDTO)
                    Assertions.assertNotNull(mediaDTO.destPath)
                    logger.debug(mediaDTO.destPath)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUploadMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val path = MediaApplicationWorkWeiXinTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaId = mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }

                try {
                    mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = randomString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    mediaApplication.uploadFile(isvAccessToken = randomString(),path = path).await()
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
}