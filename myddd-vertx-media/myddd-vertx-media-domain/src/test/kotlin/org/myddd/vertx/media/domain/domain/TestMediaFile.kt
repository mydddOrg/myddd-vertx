package org.myddd.vertx.media.domain.domain

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.media.domain.AbstractTest
import org.myddd.vertx.media.domain.MediaFile

class TestMediaFile:AbstractTest() {

    companion object {
        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }
    }

    @Test
    fun testMediaFile(testContext: VertxTestContext){
        testContext.execute {
            testContext.assertThrow(Exception::class.java){
                MediaFile.of(randomString()).await()
            }

            val path = TestMediaFile::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
            val mediaFile = MediaFile.of(path).await()
            testContext.verify {
                Assertions.assertNotNull(mediaFile)
            }

            val buffer = mediaFile.toBuffer()
            testContext.verify {
                Assertions.assertNotNull(buffer)
            }
        }
    }

}