package org.myddd.vertx.repository.hibernate

import org.myddd.vertx.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "user_table",
    indexes = [
        Index(name = "index_username",columnList = "username")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])])
class User(): BaseEntity() {

    lateinit var username:String

    var age:Int = 0

    constructor(username:String,age:Int) : this() {
        this.username = username
        this.age = age
    }

}