package org.myddd.vertx.oauth2.start

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
open class AbstractWebTest {

    protected val port = 8080
    protected val host = "127.0.0.1"

    companion object {

        private var deployId:String? = null

        @BeforeAll
        @JvmStatic
        fun beforeClass(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch {
                deployId = vertx.deployVerticle(MydddOAuth2Verticle()).await()
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterClass(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch {
                vertx.undeploy(deployId).await()
                testContext.completeNow()
            }
        }
    }

}