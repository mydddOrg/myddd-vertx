package org.myddd.vertx.domain.mock

import org.myddd.vertx.domain.BaseEntity
import javax.persistence.Entity

@Entity
class MockBaseEntity : BaseEntity() {

    lateinit var username:String
}