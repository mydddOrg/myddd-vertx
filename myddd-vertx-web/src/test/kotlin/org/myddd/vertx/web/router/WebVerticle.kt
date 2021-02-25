package org.myddd.vertx.web.router

import com.google.inject.Guice
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

class WebVerticle : CoroutineVerticle() {

    override fun start(startFuture: Promise<Void>?) {
        vertx.executeBlocking<Unit> {
            initIOC()
            it.complete()
        }.onSuccess{
            val server = vertx.createHttpServer()
            val router = Router.router(vertx)

            NotExistsRouter(vertx,router)
            UserRouter(vertx,router)

            server.requestHandler(router).listen(8080)
            startFuture?.complete()
            println("Server Started in 8080")
        }
    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
    }
}