package org.myddd.vertx.media.infra.repository

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.infra.AbstractTest

class TestMediaRepository:AbstractTest() {

    private val mediaRepository by lazy { InstanceFactory.getInstance(MediaRepository::class.java) }

    @Test
    fun testNextId(testContext: VertxTestContext){
        testContext.execute {
            Assertions.assertThat(mediaRepository.nextId()).isNull()
        }
    }

    @Test
    fun testSaveMedia(testContext: VertxTestContext){
        testContext.execute {
            val created = createMedia().await()
            testContext.verify { Assertions.assertThat(created).isNotNull }
        }
    }

    @Test
    fun testQueryByMediaId(testContext: VertxTestContext){
        testContext.execute {
            val notExistsQuery = mediaRepository.queryByMediaId(randomString()).await()
            testContext.verify { Assertions.assertThat(notExistsQuery).isNull() }

            val created = createMedia().await()
            val query = mediaRepository.queryByMediaId(created.id!!).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }
        }
    }


    @Test
    fun testQueryByDigest(testContext: VertxTestContext){
        testContext.execute {
            val notExistsQuery = mediaRepository.queryByDigest(randomString()).await()
            testContext.verify { Assertions.assertThat(notExistsQuery).isNull() }

            val created = createMedia().await()
            val query = mediaRepository.queryByDigest(created.digest).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }
        }
    }

}