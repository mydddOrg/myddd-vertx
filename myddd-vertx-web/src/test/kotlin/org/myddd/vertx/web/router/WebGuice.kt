package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider

class WebGuice(private val vertx:Vertx) : AbstractModule() {

    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
        bind(I18N::class.java).to(I18NVertxProvider::class.java)
    }
}