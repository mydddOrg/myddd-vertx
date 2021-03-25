package com.foreverht.isvgateway.bootstrap.router

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.multipart.MultipartForm
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MediaRouteTest: AbstractISVRouteTest() {

    companion object {
        private const val mediaId = "ad6a568cfbb540f0ad75d10e77d233de"
        private val logger by lazy { LoggerFactory.getLogger(MediaRouteTest::class.java) }
    }

    @Test
    fun testUploadMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val path = "META-INF/my_avatar.png"
                val absolutePath = MediaRouteTest::class.java.classLoader.getResource(path)!!.path

                val form = MultipartForm.create()
                    .binaryFileUpload("file","my_avatar.png",absolutePath, "application/octet-stream ")

                val response = webClient.post(port,host,"/v1/medias?accessToken=$accessToken")
                    .putHeader("content-type", "multipart/form-data")
                    .sendMultipartForm(form)
                    .await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val errorResponse = webClient.post(port,host,"/v1/medias?accessToken=${UUID.randomUUID()}")
                    .putHeader("content-type", "multipart/form-data")
                    .sendMultipartForm(form)
                    .await()

                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testGetMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/v1/medias/$mediaId?accessToken=$accessToken")
                    .send()
                    .await()

                testContext.verify {
                    logger.debug(response.headers())
                    Assertions.assertEquals(200,response.statusCode())
                }

                try {
                    val errorResponse = webClient.get(port,host,"/v1/medias/$mediaId?accessToken=${UUID.randomUUID()}")
                        .send()
                        .await()
                    logger.debug(errorResponse.bodyAsString())
                }catch (t:Throwable){
                    testContext.failNow(t)
                }

                try {
                    val errorResponse = webClient.get(port,host,"/v1/medias/${UUID.randomUUID()}?accessToken=$accessToken}")
                        .send()
                        .await()
                    logger.debug(errorResponse.bodyAsString())
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}