package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.start.router.MydddOAuth2Router

class MydddOAuth2Verticle : CoroutineVerticle() {


    override fun start(startFuture: Promise<Void>?) {
        vertx.executeBlocking<Unit> {
            initIOC()
            it.complete()
        }.onSuccess{
            val server = vertx.createHttpServer()
            val router = Router.router(vertx)

            router.route().order(Int.MAX_VALUE).respond { ctx ->
                val response = ctx.response()
                response.putHeader("content-type","application/json")
                response.statusCode = 403
                Future.succeededFuture(JsonObject().put("error", "API调用错误，请检查API规范"))
            }

            MydddOAuth2Router(router)

            server.requestHandler(router).listen(8080)
            println("listen to 8080")
            startFuture?.complete();
        }
    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(MydddGuiceModule())))
    }
}