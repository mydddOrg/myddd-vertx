package org.myddd.vertx.repository.hibernate

import org.myddd.vertx.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "user")
class User(): BaseEntity() {

    lateinit var userName:String

    var age:Int = 0

    constructor(username:String,age:Int) : this() {
        this.userName = username
        this.age = age
    }

}