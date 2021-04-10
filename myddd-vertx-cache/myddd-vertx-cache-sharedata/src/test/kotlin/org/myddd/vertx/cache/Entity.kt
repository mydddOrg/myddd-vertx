package org.myddd.vertx.cache

import io.vertx.core.shareddata.Shareable
import java.io.Serializable

class Entity:Shareable, Serializable {

    lateinit var name:String

    var age:Int = 0
}