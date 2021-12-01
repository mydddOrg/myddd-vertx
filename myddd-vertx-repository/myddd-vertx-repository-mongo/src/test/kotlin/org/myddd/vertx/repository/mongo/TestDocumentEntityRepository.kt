package org.myddd.vertx.repository.mongo

import io.vertx.core.json.JsonObject
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
import org.myddd.vertx.repository.mongo.ext.collectionName
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import org.myddd.vertx.repository.mongo.mock.MockEntityWithTableAnnotation

@ExtendWith(VertxExtension::class)
class TestDocumentEntityRepository:AbstractTest() {

    private val documentEntityRepository by lazy { InstanceFactory.getInstance(DocumentEntityRepository::class.java) }


    @Test
    fun testNoNull(){
        Assertions.assertThat(documentEntityRepository).isNotNull
        Assertions.assertThat((documentEntityRepository as DocumentEntityRepositoryMongo).mongoClient).isNotNull
    }

    @Test
    fun testInsertDocument(testContext: VertxTestContext){
        testContext.execute {
            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()

            testContext.verify {
                Assertions.assertThat(inserted).isNotNull
                Assertions.assertThat(inserted.id).isNotNull
            }
        }
    }

    @Test
    fun testQueryEntityById(testContext: VertxTestContext){
        testContext.execute {
            val notValidQuery = documentEntityRepository.queryEntityById(randomString(),MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }

            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryEntityById(inserted.id!!,MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }
        }
    }

    @Test
    fun testSingleQuery(testContext: VertxTestContext){
        testContext.execute {

            val notExistsQuery = documentEntityRepository.singleQuery(JsonObject().put("_id",randomString()),MockDocumentEntity::class.java).await()
            testContext.verify {
                Assertions.assertThat(notExistsQuery).isNull()
            }

            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.singleQuery(JsonObject().put("_id",inserted.id),MockDocumentEntity::class.java).await()
            testContext.verify {
                Assertions.assertThat(query).isNotNull
            }
        }
    }

    @Test
    fun testListQuery(testContext: VertxTestContext){
        testContext.execute {
            val emptyList = documentEntityRepository.listQuery(JsonObject().put("_id",randomString()),MockDocumentEntity::class.java).await()
            testContext.verify {
                Assertions.assertThat(emptyList.isEmpty()).isTrue()
            }

            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()
            val listQuery = documentEntityRepository.listQuery(JsonObject().put("_id",inserted.id),MockDocumentEntity::class.java).await()
            testContext.verify {
                Assertions.assertThat(listQuery.isNotEmpty()).isTrue()
            }
        }
    }

    @Test
    fun testDeleteEntity(testContext: VertxTestContext){
        testContext.execute {
            val inserted = documentEntityRepository.insert(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryEntityById(inserted.id!!,MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }

            documentEntityRepository.removeEntity(inserted.id!!,MockDocumentEntity::class.java).await()
            val notValidQuery = documentEntityRepository.queryEntityById(inserted.id!!,MockDocumentEntity::class.java).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }
        }
    }


}