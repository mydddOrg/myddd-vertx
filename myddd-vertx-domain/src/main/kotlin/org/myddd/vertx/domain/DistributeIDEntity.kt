package org.myddd.vertx.domain

import javax.persistence.Id
import javax.persistence.Version

abstract class DistributeIDEntity:Entity {

    @Id
    var id:Long = 0

    @Version
    var version:Long = 0

    override var created:Long = 0

    override var updated:Long = 0

    init {
        this.created = System.currentTimeMillis()
    }

    override fun getId(): Long {
        return id
    }

}