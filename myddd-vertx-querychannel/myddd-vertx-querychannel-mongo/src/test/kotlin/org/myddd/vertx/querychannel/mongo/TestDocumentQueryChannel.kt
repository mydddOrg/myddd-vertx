package org.myddd.vertx.querychannel.mongo

import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.querychannel.AbstractTest
import org.myddd.vertx.querychannel.api.DocumentQueryChannel
import org.myddd.vertx.querychannel.mock.MockUser
import org.myddd.vertx.repository.api.DocumentEntityRepository

class TestDocumentQueryChannel: AbstractTest() {
    private val documentEntityRepository by lazy { InstanceFactory.getInstance(DocumentEntityRepository::class.java) }

    private val documentQueryChannel by lazy { InstanceFactory.getInstance(DocumentQueryChannel::class.java) }

    @BeforeEach
    fun beforeEach(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.batchInsert(listOf(randomMockUser(),randomMockUser())).await()
        }
    }

    @AfterEach
    fun afterEach(testContext: VertxTestContext){
        testContext.execute {
            documentEntityRepository.removeEntities(MockUser::class.java, JsonObject())
        }
    }

    @Test
    fun testPageQuery(testContext: VertxTestContext){
        testContext.execute {
            val pageQuery = documentQueryChannel.pageQuery(clazz = MockUser::class.java).await()
            testContext.verify {
                Assertions.assertThat(pageQuery).isNotNull
                Assertions.assertThat(pageQuery.totalCount).isGreaterThan(0)
            }

            val emptyQuery = documentQueryChannel.pageQuery(clazz = MockUser::class.java, query = JsonObject().put("_id",randomString())).await()
            testContext.verify {
                Assertions.assertThat(emptyQuery).isNotNull
                Assertions.assertThat(emptyQuery.totalCount).isEqualTo(0)
            }
        }
    }

    @Test
    fun testListQuery(testContext: VertxTestContext){
        testContext.execute {
            val listQuery = documentQueryChannel.listQuery(clazz = MockUser::class.java).await()
            testContext.verify {
                Assertions.assertThat(listQuery).isNotNull
                Assertions.assertThat(listQuery.count()).isGreaterThan(0)
            }

            val emptyQuery = documentQueryChannel.listQuery(clazz = MockUser::class.java, query = JsonObject().put("_id",randomString())).await()
            testContext.verify {
                Assertions.assertThat(emptyQuery).isNotNull
                Assertions.assertThat(emptyQuery.count()).isEqualTo(0)
            }
        }
    }
}