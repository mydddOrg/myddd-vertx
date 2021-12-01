package org.myddd.vertx.domain.mock

import org.myddd.vertx.domain.BaseStringIDEntity
import javax.persistence.Entity

@Entity
class MockBaseStringIDEntity: BaseStringIDEntity() {

    private lateinit var username:String


}