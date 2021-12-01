package org.myddd.vertx.repository.mongo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.repository.mongo.ext.collectionName
import org.myddd.vertx.repository.mongo.mock.MockDocumentEntity
import org.myddd.vertx.repository.mongo.mock.MockEntityWithTableAnnotation

class TestDocumentEntityExt {

    @Test
    fun testCollectionName(){
        Assertions.assertThat(MockDocumentEntity::class.java.collectionName()).isEqualTo("MockDocumentEntity")
        Assertions.assertThat(MockEntityWithTableAnnotation::class.java.collectionName()).isEqualTo("test_name")
    }

}