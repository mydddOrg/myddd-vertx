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

abstract class BootstrapVerticle(private val port:Int = 8080) : CoroutineVerticle() {

    private val logger by lazy { LoggerFactory.getLogger(BootstrapVerticle::class.java) }


    override fun start(startFuture: Promise<Void>?) {

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                initGlobalConfig().await()

                vertx.executeBlocking<Unit> {
                    initIOC()
                    it.complete()
                }.await()

                initHttpServer().await()

                startFuture?.complete()
            }catch (e:Exception){
                logger.error("started failed:",e)
            }
        }
    }

    private fun initHttpServer(): Future<HttpServer> {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        NotExistsRouter(vertx,router)
        routers(vertx,router)()
        return server.requestHandler(router).listen(port)
    }

    private fun initIOC(){
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(abstractModules(vertx))))
    }

    private suspend fun initGlobalConfig(): Future<Unit> {
        return GlobalConfig.loadGlobalConfig(vertx)
    }

    abstract fun abstractModules(vertx: Vertx):AbstractModule

    abstract fun routers(vertx: Vertx,router: Router):()->Unit
}