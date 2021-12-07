package org.myddd.vertx.file

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.AbstractTest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import java.io.FileInputStream
import java.util.*

class FileDigestTest: AbstractTest() {

    private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }

    @Test
    fun testFileDigest(testContext: VertxTestContext){
        testContext.execute {
            try {
                fileDigest.digest(UUID.randomUUID().toString()).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }

            val digest = fileDigest.digest(FileDigestTest::class.java.classLoader.getResource("my_avatar.png")!!.path)
            testContext.verify {
                Assertions.assertNotNull(digest)
            }
        }
    }

    @Test
    fun testFileDigestForInputStream(testContext: VertxTestContext){
        testContext.execute {
            val path = FileDigestTest::class.java.classLoader.getResource("my_avatar.png")!!.path
            val digest = fileDigest.digest(FileInputStream(path))
            testContext.verify {
                Assertions.assertNotNull(digest)
            }
        }
    }
}