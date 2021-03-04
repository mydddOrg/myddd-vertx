package org.myddd.vertx.web.router

import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Promise
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

class WebVerticle : CoroutineVerticle() {

    private val logger by lazy { LoggerFactory.getLogger(WebVerticle::class.java) }

    override fun start(startFuture: Promise<Void>?) {

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                vertx.executeBlocking<Unit> {
                    initIOC()
                    it.complete()
                }.await()

                initGlobalConfig().await()
                initHttpServer().await()

                startFuture?.complete()
            }catch (e:Exception){
                logger.error("started failed:",e)
            }
        }
    }

    private suspend fun initHttpServer():Future<HttpServer>{
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        NotExistsRouter(vertx,router)
        UserRouter(vertx,router)

        return server.requestHandler(router).listen(8080)
    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
    }

    private suspend fun initGlobalConfig():Future<Unit>{
        return GlobalConfig.loadGlobalConfig()
    }
}