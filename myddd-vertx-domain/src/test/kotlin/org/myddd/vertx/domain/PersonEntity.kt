package org.myddd.vertx.domain

import javax.persistence.Entity

@Entity
class PersonEntity:BaseStringIDEntity() {

    private lateinit var username:String


}