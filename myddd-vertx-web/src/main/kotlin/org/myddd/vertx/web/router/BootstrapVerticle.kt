package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.spi.resolver.ResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.config.Config

abstract class BootstrapVerticle(private val port:Int = 8080) : CoroutineVerticle() {

    private val logger by lazy { LoggerFactory.getLogger(BootstrapVerticle::class.java) }

    private lateinit var server:HttpServer;


    private val startedPort:Int by lazy {
        Config.getInteger("port",port)
    }
    override suspend fun start() {
        super.start()
        initIOC(vertx)
        initGlobalConfig().await()
        //禁用Vert.x的DNS解析逻辑
        System.getProperties().setProperty(DISABLE_DNS_RESOLVER_PROP_NAME,"true")

        initHttpServer().await()
        logger.info("Started in port: $startedPort")
    }

    private fun initHttpServer(): Future<HttpServer> {
        server = vertx.createHttpServer()
        val router = Router.router(vertx)
        NotExistsRouter(vertx,router)
        routers(vertx,router)()
        return server.requestHandler(router).listen(startedPort)
    }

    abstract suspend fun initIOC(vertx: Vertx)

    private suspend fun initGlobalConfig(): Future<Unit> {
        return Config.loadGlobalConfig(vertx)
    }

    abstract fun routers(vertx: Vertx,router: Router):()->Unit

    override suspend fun stop() {
        super.stop()
        server.close().await()
    }
}