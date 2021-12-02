package org.myddd.vertx.repository.mongo.mock

import org.myddd.vertx.domain.DocumentEntity
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name="media_",
    indexes = [
        Index(name = "index_digest",columnList = "digest")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "unique_digest",columnNames = ["digest"])
    ])
class MockMedia:DocumentEntity() {

    lateinit var digest:String

    lateinit var name:String
}