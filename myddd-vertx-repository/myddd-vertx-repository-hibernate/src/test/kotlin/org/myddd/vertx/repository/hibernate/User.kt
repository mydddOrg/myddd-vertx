package org.myddd.vertx.repository.hibernate

import org.myddd.vertx.domain.BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_table",
    indexes = [
        Index(name = "index_username",columnList = "username")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])])
class User(): BaseEntity() {

    @Column(name = "username",length = 64)
    lateinit var username:String

    var age:Int = 0

    constructor(username:String,age:Int) : this() {
        this.username = username
        this.age = age
    }

    override fun equals(other: Any?): Boolean {
        if(other !is User)return false
        return Objects.equals(getId(),other.getId())
    }

    override fun hashCode(): Int {
        return Objects.hashCode(getId())
    }

}