package org.myddd.vertx.oauth2.start

import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.start.router.OAuth2ClientRouter
import org.myddd.vertx.oauth2.start.router.OAuth2TokenRouter
import org.myddd.vertx.config.Config

class OAuth2Verticle(private val port:Int = 8080) : CoroutineVerticle() {


    override suspend fun start() {
        super.start()
        initGlobalConfig().await()
        vertx.executeBlocking<Unit> {
            initIOC()
            it.complete()
        }.await()
        initHttpServer().await()
    }

    private fun initHttpServer(): Future<HttpServer> {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.route().order(Int.MAX_VALUE).respond { ctx ->
            val response = ctx.response()
            response.putHeader("content-type","application/json")
            response.statusCode = 404
            Future.succeededFuture(JsonObject()
                .put("errorCode", "NO_SUCH_API")
                .put("errorMsg","调用的API不存在"))
        }

        OAuth2ClientRouter(router = router,vertx = vertx)
        OAuth2TokenRouter(router = router,vertx = vertx)

        return server.requestHandler(router).listen(8080)
    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(OAuth2GuiceModule(vertx))))
    }

    private suspend fun initGlobalConfig(): Future<Unit> {
        return Config.loadGlobalConfig(vertx)
    }
}