package org.myddd.vertx.domain


import javax.persistence.*

/**
 * 基于ID的一个基类
 */
@MappedSuperclass
abstract class BaseEntity : Entity {

    companion object {

        private var idGenerator:SnowflakeDistributeId

        init {

            val workerId = try {
                System.getenv("workerId").toLong()
            }catch (e:Exception){
                0L
            }

            val datacenterId = try {
                System.getenv("datacenterId").toLong()
            }catch (e:Exception){
                0L
            }

            idGenerator = SnowflakeDistributeId(workerId = workerId ,datacenterId = datacenterId)

        }
    }

    @Id
    var id:Long = 0

    @Version
    var version:Long = 0

    override var created:Long = 0

    override var updated:Long = 0

    init {
        this.id = idGenerator.nextId()
        this.created = System.currentTimeMillis()
    }

    override fun getId(): Long {
        return id
    }
}