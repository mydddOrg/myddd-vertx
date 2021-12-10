package org.myddd.vertx.domain.mock

import org.myddd.vertx.domain.BaseAutoIDEntity
import javax.persistence.Entity

@Entity
class MockBaseAutoIDEntity:BaseAutoIDEntity() {
    lateinit var username:String
}