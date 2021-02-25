package org.myddd.vertx.domain

import java.io.Serializable

interface Entity : Serializable{
    fun getId():Serializable
}