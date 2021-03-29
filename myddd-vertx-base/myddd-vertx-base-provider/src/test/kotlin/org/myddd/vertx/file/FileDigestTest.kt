package org.myddd.vertx.file

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.AbstractTest
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class FileDigestTest: AbstractTest() {

    private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }

    @Test
    fun testFileDigest(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

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
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}