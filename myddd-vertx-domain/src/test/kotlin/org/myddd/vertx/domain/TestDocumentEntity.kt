package org.myddd.vertx.domain

import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.mock.MockDocumentEntity
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString

class TestDocumentEntity:AbstractDomainTest() {

    @Test
    fun testDocumentEntity(testContext: VertxTestContext){
        testContext.execute {
            val mockDocumentEntity = MockDocumentEntity()
            mockDocumentEntity.name = randomString()
            mockDocumentEntity.id = randomString()

            testContext.verify {
                Assertions.assertThat(mockDocumentEntity.id).isNotNull
                Assertions.assertThat(mockDocumentEntity.name).isNotNull
            }

            val jsonObject = JsonObject.mapFrom(mockDocumentEntity)

            testContext.verify {
                Assertions.assertThat(jsonObject.getString("_id")).isNotNull()
            }
        }

    }

}