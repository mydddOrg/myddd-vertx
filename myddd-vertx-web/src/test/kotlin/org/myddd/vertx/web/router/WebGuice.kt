package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx

class WebGuice(private val vertx:Vertx) : AbstractModule() {

    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
    }
}