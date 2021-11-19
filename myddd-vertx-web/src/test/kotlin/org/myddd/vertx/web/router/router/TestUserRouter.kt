package org.myddd.vertx.web.router.router

import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.AbstractRouterTest
import org.myddd.vertx.web.router.WebGuice
import org.myddd.vertx.web.router.WebVerticle
import java.lang.RuntimeException
import java.util.*

class TestUserRouter:AbstractRouterTest() {

    val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    companion object{

        val logger = LoggerFactory.getLogger(AbstractRouterTest::class.java)

        var port = 8080

        const val host = "127.0.0.1"

        private lateinit var deployId:String

        val oAuth2Application: OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx:Vertx,testContext: VertxTestContext){
            testContext.execute {
                InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
                deployId = vertx.deployVerticle(WebVerticle(port = port)).await()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterClass(vertx: Vertx, testContext: VertxTestContext){
            testContext.execute {
                vertx.undeploy(deployId).await()
            }
        }

        fun <T> any(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        private fun <T> uninitialized(): T = null as T

    }

    @Test
    fun testGetRoute(vertx: Vertx, testContext: VertxTestContext){
        testContext.execute {
            var response = webClient.get(port,host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }

            response = webClient.get(port,host,"/v1/users?error=true")
                .send()
                .await()

            testContext.verify {
                logger.debug(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }


    @Test
    fun testPostRoute(vertx: Vertx, testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()
            var response = webClient.post(port,host,"/v1/users")
                .sendJsonObject(JsonObject().put("userId",userId))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val responseBody = response.bodyAsJsonObject()
                Assertions.assertEquals(userId,responseBody.getString("userId"))
            }

            response = webClient.post(port,host,"/v1/users?error=true")
                .sendJsonObject(JsonObject().put("userId",userId))
                .await()

            testContext.verify {
                logger.info(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }

            response = webClient.post(port,host,"/v1/users?error=true")
                .sendJsonObject(JsonObject())
                .await()

            testContext.verify {
                logger.error(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testPutRoute(vertx: Vertx, testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()
            val name = UUID.randomUUID().toString()

            var response = webClient.put(port,host,"/v1/users/$userId")
                .sendJsonObject(JsonObject().put("name",name))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val bodyJson = response.bodyAsJsonObject()

                Assertions.assertEquals(userId,bodyJson.getString("userId"))
                Assertions.assertEquals(name,bodyJson.getString("name"))
            }

            response = webClient.put(port,host,"/v1/users/$userId?error=true")
                .sendJsonObject(JsonObject().put("name",name))
                .await()

            testContext.verify {
                logger.debug(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testPatchRoute(vertx: Vertx, testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()

            var response = webClient.patch(port,host,"/v1/users/$userId")
                .sendJsonObject(JsonObject().put("name", UUID.randomUUID().toString()))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val bodyJson = response.bodyAsJsonObject()

                Assertions.assertEquals(userId,bodyJson.getString("userId"))
                Assertions.assertNotNull(bodyJson.getString("name"))
            }

            response = webClient.patch(port,host,"/v1/users/$userId?error=true")
                .sendJsonObject(JsonObject().put("name", UUID.randomUUID().toString()))
                .await()

            testContext.verify {
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testDeleteRoute(vertx: Vertx, testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()

            var response = webClient.delete(port,host,"/v1/users/$userId")
                .send()
                .await()

            testContext.verify {
                Assertions.assertEquals(204,response.statusCode())
            }

            response = webClient.delete(port,host,"/v1/users/$userId?error=true")
                .send()
                .await()

            testContext.verify {
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testNotExistsRoute(vertx:Vertx,testContext: VertxTestContext){
        testContext.execute {
            val response = webClient.get(port,host,"/${UUID.randomUUID()}")
                .send().await()
            logger.debug(response.getHeader("Content-Type"))
            testContext.verify { Assertions.assertEquals(404,response.statusCode()) }
        }
    }

    @Test
    fun testAuthorizationGetRoute(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            val future = Mockito.mock(Future::class.java)
            Mockito.`when`(future.succeeded()).thenReturn(false)
            Mockito.`when`(future.failed()).thenReturn(true)
            Mockito.`when`(future.cause()).thenReturn(RuntimeException())
            Mockito.`when`(oAuth2Application.queryValidClientIdByAccessToken(any())).thenReturn(future as Future<String>?)

            var response = webClient.get(port,host,"/v1/authorization/users").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }

            response = webClient.get(port,host,"/v1/authorization/users?accessToken=${UUID.randomUUID()}").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }

            Mockito.`when`(future.succeeded()).thenReturn(true)
            Mockito.`when`(future.failed()).thenReturn(false)
            Mockito.`when`(future.result()).thenReturn(UUID.randomUUID().toString())

            response = webClient.get(port,host,"/v1/authorization/users?accessToken=${UUID.randomUUID()}").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }
        }
    }

}