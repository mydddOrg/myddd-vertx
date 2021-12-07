package org.myddd.vertx.oauth2.start

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
open class AbstractWebTest {


    companion object {

        val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }


        val logger by lazy { LoggerFactory.getLogger(AbstractWebTest::class.java) }

        var port = 8080

        const val host = "127.0.0.1"

        private var deployId:String? = null

        val webClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        @BeforeAll
        @JvmStatic
        fun startVerticle(testContext: VertxTestContext){
            testContext.execute {
                deployId = vertx.deployVerticle(OAuth2Verticle(port = port)).await()
            }
        }

        @JvmStatic
        @AfterAll
        fun stopVerticle(testContext: VertxTestContext){
            testContext.execute {
                vertx.undeploy(deployId).await()
            }
        }

    }

}