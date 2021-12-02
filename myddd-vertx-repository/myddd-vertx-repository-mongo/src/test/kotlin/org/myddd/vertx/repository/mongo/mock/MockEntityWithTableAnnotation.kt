package org.myddd.vertx.repository.mongo.mock

import org.myddd.vertx.domain.DocumentEntity
import javax.persistence.Table

@Table(name = "test_name")
class MockEntityWithTableAnnotation: DocumentEntity() {
}