package org.myddd.vertx.repository.mongo

import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.assertNotThrow
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.repository.AbstractTest
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.api.QueryOptions
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity

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
            val created = documentEntityRepository.save(randomMockDocumentEntity()).await()

            testContext.verify {
                Assertions.assertThat(created).isNotNull
                Assertions.assertThat(created.id).isNotNull
            }

            created.name = randomString()
            val updated = documentEntityRepository.save(created).await()
            testContext.verify {
                Assertions.assertThat(updated).isNotNull
                Assertions.assertThat(updated.id).isEqualTo(created.id)
            }
        }
    }

    @Test
    fun testQueryEntityById(testContext: VertxTestContext){
        testContext.execute {
            val notValidQuery = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, randomString()).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }

            val inserted = documentEntityRepository.save(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, inserted.id!!).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }
        }
    }

    @Test
    fun testQueryAllNames(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository
        }
    }

    @Test
    fun testSingleQuery(testContext: VertxTestContext){
        testContext.execute {

            val notExistsQuery = documentEntityRepository.singleQuery(
                MockDocumentEntity::class.java,
                JsonObject().put("_id",randomString())
            ).await()
            testContext.verify {
                Assertions.assertThat(notExistsQuery).isNull()
            }

            val inserted = documentEntityRepository.save(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.singleQuery(
                MockDocumentEntity::class.java,
                JsonObject().put("_id",inserted.id)
            ).await()
            testContext.verify {
                Assertions.assertThat(query).isNotNull
            }
        }
    }

    @Test
    fun testListQuery(testContext: VertxTestContext){
        testContext.execute {
            val emptyList = documentEntityRepository.listQuery(
                MockDocumentEntity::class.java,
                JsonObject().put("_id",randomString())
            ).await()
            testContext.verify {
                Assertions.assertThat(emptyList.isEmpty()).isTrue()
            }

            val inserted = documentEntityRepository.save(randomMockDocumentEntity()).await()
            val listQuery = documentEntityRepository.listQuery(
                MockDocumentEntity::class.java,
                JsonObject().put("_id",inserted.id)
            ).await()
            testContext.verify {
                Assertions.assertThat(listQuery.isNotEmpty()).isTrue()
            }
        }
    }

    @Test
    fun testListQueryWithOptions(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.save(randomMockDocumentEntity()).await()

            val queryOptions = QueryOptions(fields = JsonObject().put("name",true))
            val query = documentEntityRepository.listQueryWithOptions(
                MockDocumentEntity::class.java,
                JsonObject(),
                queryOptions
            ).await()
            testContext.verify { Assertions.assertThat(query.isEmpty()).isFalse() }
        }
    }

    @Test
    fun testDeleteEntity(testContext: VertxTestContext){
        testContext.execute {
            val inserted = documentEntityRepository.save(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, inserted.id!!).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }

            documentEntityRepository.removeEntity(MockDocumentEntity::class.java, inserted.id!!).await()
            val notValidQuery = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, inserted.id!!).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }
        }
    }


    @Test
    fun testRemoveEntities(testContext: VertxTestContext){
        testContext.execute {
            val inserted = documentEntityRepository.save(randomMockDocumentEntity()).await()
            val query = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, inserted.id!!).await()
            testContext.verify { Assertions.assertThat(query).isNotNull }

            val count = documentEntityRepository.removeEntities(MockDocumentEntity::class.java, JsonObject()).await()
            logger.debug(count)
            testContext.verify {
                Assertions.assertThat(count).isGreaterThan(0)
            }
            val notValidQuery = documentEntityRepository.queryEntityById(MockDocumentEntity::class.java, inserted.id!!).await()
            testContext.verify { Assertions.assertThat(notValidQuery).isNull() }
        }
    }


    @Test
    fun testBatchInsert(testContext: VertxTestContext){
        testContext.execute {
            val adds = listOf(randomMockDocumentEntity(),randomMockDocumentEntity())
            testContext.assertNotThrow {
                documentEntityRepository.batchInsert(adds).await()
            }

            testContext.assertThrow(IllegalArgumentException::class.java){
                documentEntityRepository.batchInsert(listOf()).await()
            }
        }
    }
}