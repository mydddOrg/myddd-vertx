package org.myddd.vertx.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.ext.collectionName
import org.myddd.vertx.domain.ext.indexes
import org.myddd.vertx.domain.ext.uniqueConstraints
import org.myddd.vertx.domain.mock.MockMedia

class TestDocumentExt {

    @Test
    fun testCollectionName(){
        val mockMedia = MockMedia()
        Assertions.assertThat(mockMedia.collectionName()).isEqualTo("media_")
        Assertions.assertThat(MockMedia::class.java.collectionName()).isEqualTo("media_")
    }

    @Test
    fun testIndexes(){
        val indexes = MockMedia::class.java.indexes()
        Assertions.assertThat(indexes.isNotEmpty()).isTrue
    }

    @Test
    fun testUniqueConstraints(){
        val uniqueConstraints = MockMedia::class.java.uniqueConstraints()
        Assertions.assertThat(uniqueConstraints.isNotEmpty()).isTrue
    }

}