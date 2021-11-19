package org.myddd.vertx.web.router

import com.google.inject.Guice
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import org.mockito.Mockito.mock
import org.myddd.vertx.junit.execute
import java.lang.RuntimeException

@ExtendWith(VertxExtension::class)
open class AbstractRouterTest {

    @Test
    fun testInstanceFactory(vertx:Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java))
        }
        testContext.completeNow()
    }

    @Test
    fun testLoadGlobalConfig(vertx: Vertx,testContext: VertxTestContext){
        val path = "META-INF/config.properties"

        val fileStore = ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(JsonObject().put("path", path))

        val options = ConfigRetrieverOptions()
            .addStore(fileStore)
        val configRetriever = ConfigRetriever.create(vertx, options)

        configRetriever.getConfig {
            if(it.succeeded()){
                println(it.result())
                testContext.completeNow()
            }else{
                testContext.failNow(it.cause())
            }
        }
    }


}