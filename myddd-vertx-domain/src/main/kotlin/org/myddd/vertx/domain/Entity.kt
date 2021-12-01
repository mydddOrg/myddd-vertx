package org.myddd.vertx.domain

import java.io.Serializable

interface Entity : Serializable{

    var created:Long

    var updated:Long

    fun getId():Serializable

    fun setId(id:Serializable)
}