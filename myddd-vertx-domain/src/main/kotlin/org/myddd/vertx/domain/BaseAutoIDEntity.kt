package org.myddd.vertx.domain

import java.io.Serializable
import javax.persistence.*

@MappedSuperclass
open class BaseAutoIDEntity:Entity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    var id:Long = 0

    @Version
    var version:Long = 0

    final override var created:Long = System.currentTimeMillis()

    override var updated:Long = 0

    init {
        this.created = System.currentTimeMillis()
    }

    override fun getId(): Long {
        return id
    }

    override fun setId(id: Serializable) {
        this.id = id as Long
    }
}