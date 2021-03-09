package org.myddd.vertx.oauth2.start

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
open class AbstractWebTest {

    protected val port = 8080
    protected val host = "127.0.0.1"

    companion object {

        private var deployId:String? = null

        lateinit var webClient: WebClient

        @BeforeAll
        @JvmStatic
        fun beforeClass(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                webClient = WebClient.create(vertx)
                deployId = vertx.deployVerticle(OAuth2Verticle()).await()
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterClass(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                vertx.undeploy(deployId).await()
                testContext.completeNow()
            }
        }
    }

    @Test
    fun testNoExistsRequest(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient:WebClient = WebClient.create(vertx)
                val response = webClient.get(port,host,"/v1/notExistsRequest").send().await()
                testContext.verify { Assertions.assertEquals(404,response.statusCode()) }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

}