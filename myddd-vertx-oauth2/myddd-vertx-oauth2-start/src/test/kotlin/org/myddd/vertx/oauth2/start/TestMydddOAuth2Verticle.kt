package org.myddd.vertx.oauth2.start

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestMydddOAuth2Verticle  {


    @Test
    fun testStart(vertx:Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            val deployId = vertx.deployVerticle(MydddOAuth2Verticle()).await()
            testContext.verify {
                Assertions.assertNotNull(deployId)
            }
            val webClient = WebClient.create(vertx)
            val response = webClient.get(8080,"127.0.0.1","/a").send().await()
            testContext.verify {
                Assertions.assertEquals(response.statusCode(), 403)
            }

            val responseForHello = webClient.get(8080,"127.0.0.1","/hello").send().await()
            testContext.verify {
                Assertions.assertEquals(responseForHello.statusCode(), 200)
            }

            testContext.completeNow()
        }
    }
}