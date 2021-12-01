package org.myddd.vertx.repository.mongo

import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.repository.AbstractTest
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import java.util.*

@ExtendWith(VertxExtension::class)
class TestDocumentEntityRepository:AbstractTest() {

    private val documentEntityRepository by lazy { InstanceFactory.getInstance(DocumentEntityRepository::class.java) }

    @Test
    fun testInsertDocument(testContext: VertxTestContext){
        testContext.execute {
            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()

            testContext.verify {
                Assertions.assertThat(inserted).isNotNull
                Assertions.assertThat(inserted!!.id).isNotNull
            }
        }
    }

    @Test
    fun testQueryById(testContext: VertxTestContext){
        testContext.execute {
            val notValidQuery = documentEntityRepository.queryById(randomString(),MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }

            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryById(inserted!!.id!!,MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }
        }
    }




}