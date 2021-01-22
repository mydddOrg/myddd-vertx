package org.myddd.vertx.domain

/**
 * 基于ID的一个基类
 */
class BaseEntity : Entity {

    var id:Long = 0

    var version:Long = 0

    var created:Long = 0

    var updated:Long = 0
}