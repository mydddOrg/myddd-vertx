package org.myddd.vertx.domain

import java.io.Serializable
import javax.persistence.*

/**
 * 基于ID的一个基类
 */
@MappedSuperclass
abstract class BaseEntity : Entity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    var id:Long = 0

    @Version
    var version:Long = 0

    var created:Long = 0

    var updated:Long = 0

    override fun getId(): Serializable {
        return id
    }
}