package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.web.router.config.GlobalConfig

class WebVerticle(port:Int = 8080) : BootstrapVerticle(port = port) {

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return WebGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return  {
            UserRouter(vertx,router)
        }
    }
}