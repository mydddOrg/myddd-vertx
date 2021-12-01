package org.myddd.vertx.repository.mongo.mock

import org.myddd.vertx.domain.DocumentEntity
import javax.persistence.Entity

@Entity(name = "test_document_")
class MockDocumentEntity:DocumentEntity() {
    lateinit var name:String
}