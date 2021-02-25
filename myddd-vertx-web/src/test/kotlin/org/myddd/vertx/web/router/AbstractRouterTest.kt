package org.myddd.vertx.web.router

import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import java.util.*
import kotlin.Exception

@ExtendWith(VertxExtension::class)
class AbstractRouterTest {

    companion object{
        private const val port = 8080
        private const val host = "127.0.0.1"

        private lateinit var deployId:String

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx:Vertx,testContext: VertxTestContext){
            GlobalScope.launch {
                InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
                deployId = vertx.deployVerticle(WebVerticle()).await()
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


    @Test
    fun testInstanceFactory(vertx:Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java))
        }
        testContext.completeNow()
    }

    @Test
    fun testNotExistsRoute(vertx:Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val response = webClient.get(port,host,"/${UUID.randomUUID()}")
                    .send().await()
                testContext.verify { Assertions.assertEquals(404,response.statusCode()) }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @Test
    fun testGetRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val response = webClient.get(port,host,"/v1/users").send().await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testPostRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val userId = UUID.randomUUID().toString()
                val response = webClient.post(port,host,"/v1/users")
                    .sendJsonObject(JsonObject().put("userId",userId))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                    val responseBody = response.bodyAsJsonObject()
                    Assertions.assertEquals(userId,responseBody.getString("userId"))
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testPutRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val userId = UUID.randomUUID().toString()
                val name = UUID.randomUUID().toString()

                val response = webClient.put(port,host,"/v1/users/$userId")
                    .sendJsonObject(JsonObject().put("name",name))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                    val bodyJson = response.bodyAsJsonObject()

                    Assertions.assertEquals(userId,bodyJson.getString("userId"))
                    Assertions.assertEquals(name,bodyJson.getString("name"))
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testPatchRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val userId = UUID.randomUUID().toString()

                val response = webClient.patch(port,host,"/v1/users/$userId")
                    .sendJsonObject(JsonObject().put("name",UUID.randomUUID().toString()))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                    val bodyJson = response.bodyAsJsonObject()

                    Assertions.assertEquals(userId,bodyJson.getString("userId"))
                    Assertions.assertNotNull(bodyJson.getString("name"))
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testDeleteRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val webClient = WebClient.create(vertx)
                val userId = UUID.randomUUID().toString()

                val response = webClient.delete(port,host,"/v1/users/$userId")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(204,response.statusCode())
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }


}