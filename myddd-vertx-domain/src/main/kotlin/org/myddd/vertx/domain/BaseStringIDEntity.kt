package org.myddd.vertx.domain

import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.ioc.InstanceFactory
import java.io.Serializable
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
abstract class BaseStringIDEntity:Entity {

    companion object {
        private val idGenerator by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }
    }

    @Id
    var id:String = idGenerator.nextId()

    @Version
    var version:Long = 0

    final override var created:Long = 0

    override var updated:Long = 0

    init {
        this.created = System.currentTimeMillis()
    }

    override fun getId(): Serializable {
        return id
    }
}