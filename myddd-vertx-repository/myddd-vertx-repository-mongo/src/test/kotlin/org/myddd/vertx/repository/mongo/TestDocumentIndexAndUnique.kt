package org.myddd.vertx.repository.mongo

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.ext.collectionName
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.repository.AbstractTest
import org.myddd.vertx.repository.api.DocumentEntityRepository
import org.myddd.vertx.repository.mongo.mock.MockMedia

class TestDocumentIndexAndUnique:AbstractTest() {

    private val documentEntityRepository by lazy { InstanceFactory.getInstance(DocumentEntityRepository::class.java) as DocumentEntityRepositoryMongo }


    @BeforeEach
    fun beforeEach(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.mongoClient.dropCollection(MockMedia::class.java.collectionName())
            documentEntityRepository.save(randomMockMedia()).await()
        }
    }

    @AfterEach
    fun afterAll(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.mongoClient.dropCollection(MockMedia::class.java.collectionName())
        }
    }


    @Test
    fun testCreateDocument(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.createDocument(MockMedia::class.java).await()
            val indexes = documentEntityRepository.mongoClient.listIndexes(MockMedia::class.java.collectionName()).await()
            logger.debug(indexes)
            testContext.verify { Assertions.assertThat(indexes.isEmpty).isFalse }
        }
    }
}