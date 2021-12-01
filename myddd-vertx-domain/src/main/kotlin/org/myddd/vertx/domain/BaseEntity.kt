package org.myddd.vertx.domain


import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.ioc.InstanceFactory
import java.io.Serializable
import javax.persistence.*

/**
 * 基于ID的一个基类
 */
@MappedSuperclass
abstract class BaseEntity : Entity {

    companion object {
        private val idGenerator by lazy { InstanceFactory.getInstance(IDGenerator::class.java) }
    }

    @Id
    var id:Long = 0

    @Version
    var version:Long = 0

    final override var created:Long = System.currentTimeMillis()

    override var updated:Long = 0

    init {
        this.id = idGenerator.nextId()
        this.created = System.currentTimeMillis()
    }

    override fun getId(): Long {
        return id
    }

    override fun setId(id: Serializable) {
        this.id = id as Long
    }
}