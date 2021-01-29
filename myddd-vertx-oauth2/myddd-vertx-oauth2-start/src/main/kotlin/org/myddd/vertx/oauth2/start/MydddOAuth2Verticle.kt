package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

class MydddOAuth2Verticle : CoroutineVerticle() {


    override suspend fun start() {
        vertx.executeBlocking<Unit> {
            initIOC()
            it.complete()
        }.onSuccess{
            val server = vertx.createHttpServer()
            val route = Router.router(vertx)

            route.route().handler { ctx ->
                val response = ctx.response()
                response.putHeader("content-type","application/json")
                response.end("Hello World from Vert.x-Web!");
            }

            server.requestHandler(route).listen(8080)
            println("listen to 8080")
        }

    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(MydddGuiceModule())))
    }
}