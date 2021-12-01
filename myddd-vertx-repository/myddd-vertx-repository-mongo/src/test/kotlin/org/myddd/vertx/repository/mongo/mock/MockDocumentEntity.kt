package org.myddd.vertx.repository.mongo.mock

import org.myddd.vertx.domain.DocumentEntity

class MockDocumentEntity:DocumentEntity() {
    lateinit var name:String
}