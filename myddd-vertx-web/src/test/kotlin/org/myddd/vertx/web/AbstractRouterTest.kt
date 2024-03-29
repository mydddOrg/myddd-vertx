package org.myddd.vertx.web

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.myddd.vertx.base.BadAuthorizationException
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.handler.IPFilterHandler
import java.util.*

@ExtendWith(VertxExtension::class,VerticleExtension::class)
class AbstractRouterTest {

    private val jsonConfig = mock(JsonObject::class.java)

    init {
        Mockito.`when`(jsonConfig.getString(any(),any())).thenReturn("")
        Mockito.`when`(jsonConfig.getBoolean(any(),any())).thenReturn(false)
        Mockito.`when`(jsonConfig.getInteger(any(),any())).thenReturn(0)
    }

    companion object{

        private val logger = LoggerFactory.getLogger(AbstractRouterTest::class.java)

        private var port = 8080

        private const val host = "127.0.0.1"

        private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        private val oAuth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

        val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
    }


    @Test
    fun testInstanceFactory(testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java))
        }
        testContext.completeNow()
    }

    @BeforeEach
    fun disableIpFilter(testContext: VertxTestContext){
        testContext.execute {
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(false)
            Config.configObject = jsonConfig
            IPFilterHandler.reloadCache()
        }
    }

    @Test
    fun testNotExistsRoute(testContext: VertxTestContext){
        testContext.execute {
            val response = webClient.get(port, host,"/${UUID.randomUUID()}")
                .send().await()
            logger.debug(response.getHeader("Content-Type"))
            testContext.verify { Assertions.assertEquals(404,response.statusCode()) }
        }
    }

    @Test
    fun testAsyncMock(testContext: VertxTestContext){
        testContext.execute {
            val future = mock(Future::class.java)
            Mockito.`when`(future.succeeded()).thenReturn(true)
            Mockito.`when`(future.failed()).thenReturn(false)
            Mockito.`when`(future.result()).thenReturn(UUID.randomUUID().toString())

            Mockito.`when`(oAuth2Application.queryValidClientIdByAccessToken(any())).thenReturn(future as Future<String>?)


            val clientId = oAuth2Application.queryValidClientIdByAccessToken(UUID.randomUUID().toString()).await()

            testContext.verify {
                Assertions.assertNotNull(clientId)
            }

            Mockito.`when`(future.succeeded()).thenReturn(false)
            Mockito.`when`(future.failed()).thenReturn(true)
            Mockito.`when`(future.cause()).thenReturn(RuntimeException())

            try {
                oAuth2Application.queryValidClientIdByAccessToken(UUID.randomUUID().toString()).await()
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    @Test
    fun testAuthorizationGetRoute(testContext: VertxTestContext){
        testContext.execute {
            val future = mock(Future::class.java)
            Mockito.`when`(future.succeeded()).thenReturn(false)
            Mockito.`when`(future.failed()).thenReturn(true)
            Mockito.`when`(future.cause()).thenReturn(BadAuthorizationException())
            Mockito.`when`(oAuth2Application.queryValidClientIdByAccessToken(any())).thenReturn(future as Future<String>?)

            var response = webClient.get(port, host,"/v1/authorization/users").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }

            response = webClient.get(port, host,"/v1/authorization/users?accessToken=${UUID.randomUUID()}").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }

            Mockito.`when`(future.succeeded()).thenReturn(true)
            Mockito.`when`(future.failed()).thenReturn(false)
            Mockito.`when`(future.result()).thenReturn(UUID.randomUUID().toString())

            response = webClient.get(port, host,"/v1/authorization/users?accessToken=${UUID.randomUUID()}").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }
        }
    }

    @Test
    fun testGetRoute(testContext: VertxTestContext){
        testContext.execute {
            var response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }

            response = webClient.get(port, host,"/v1/users?error=true")
                .send()
                .await()

            testContext.verify {
                logger.debug(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }


    @Test
    fun testPostRoute(testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()
            var response = webClient.post(port, host,"/v1/users")
                .sendJsonObject(JsonObject().put("userId",userId))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val responseBody = response.bodyAsJsonObject()
                Assertions.assertEquals(userId,responseBody.getString("userId"))
            }

            response = webClient.post(port, host,"/v1/users?error=true")
                .sendJsonObject(JsonObject().put("userId",userId))
                .await()

            testContext.verify {
                logger.info(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }

            response = webClient.post(port, host,"/v1/users?error=true")
                .sendJsonObject(JsonObject())
                .await()

            testContext.verify {
                logger.error(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testPutRoute(testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()
            val name = UUID.randomUUID().toString()

            var response = webClient.put(port, host,"/v1/users/$userId")
                .sendJsonObject(JsonObject().put("name",name))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val bodyJson = response.bodyAsJsonObject()

                Assertions.assertEquals(userId,bodyJson.getString("userId"))
                Assertions.assertEquals(name,bodyJson.getString("name"))
            }

            response = webClient.put(port, host,"/v1/users/$userId?error=true")
                .sendJsonObject(JsonObject().put("name",name))
                .await()

            testContext.verify {
                logger.debug(response.bodyAsString())
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testPatchRoute(testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()

            var response = webClient.patch(port, host,"/v1/users/$userId")
                .sendJsonObject(JsonObject().put("name",UUID.randomUUID().toString()))
                .await()

            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
                val bodyJson = response.bodyAsJsonObject()

                Assertions.assertEquals(userId,bodyJson.getString("userId"))
                Assertions.assertNotNull(bodyJson.getString("name"))
            }

            response = webClient.patch(port, host,"/v1/users/$userId?error=true")
                .sendJsonObject(JsonObject().put("name",UUID.randomUUID().toString()))
                .await()

            testContext.verify {
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testDeleteRoute(testContext: VertxTestContext){
        testContext.execute {
            val userId = UUID.randomUUID().toString()

            var response = webClient.delete(port, host,"/v1/users/$userId")
                .send()
                .await()

            testContext.verify {
                Assertions.assertEquals(204,response.statusCode())
            }

            response = webClient.delete(port, host,"/v1/users/$userId?error=true")
                .send()
                .await()

            testContext.verify {
                Assertions.assertEquals(400,response.statusCode())
            }
        }
    }

    @Test
    fun testLoadGlobalConfig(testContext: VertxTestContext){
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


    @Test
    fun testNoIpFilter(testContext: VertxTestContext){
        testContext.execute {
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
            Config.configObject = jsonConfig

            testContext.verify {
                Assertions.assertEquals(false,jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE))
                Assertions.assertEquals(false,
                    Config.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE))
            }

            IPFilterHandler.reloadCache()

            val response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }
        }
    }

    @Test
    fun testIpFilterWhite(testContext: VertxTestContext){
        testContext.execute {
            //启用IP白名单,包括自己
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(true)
            Mockito.`when`(jsonConfig.getString(IPFilterHandler.WHITE_LIST_VALUES)).thenReturn("127.0.0.1")
            Config.configObject = jsonConfig

            IPFilterHandler.reloadCache()

            val response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }
        }
    }

    @Test
    fun testIpFilterWhiteNotInclude(testContext: VertxTestContext){
        testContext.execute {
            //启用IP白名单,包括自己
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE,false)).thenReturn(true)
            Mockito.`when`(jsonConfig.getString(IPFilterHandler.WHITE_LIST_VALUES,"")).thenReturn("127.0.0.2")
            Config.configObject = jsonConfig
            IPFilterHandler.reloadCache()


            val response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }
        }
    }

    @Test
    fun testIpFilterBlack(testContext: VertxTestContext){
        testContext.execute {
            //启用IP白名单,包括自己
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE,false)).thenReturn(false)
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE,false)).thenReturn(true)

            Mockito.`when`(jsonConfig.getString(IPFilterHandler.BLACK_LIST_VALUES,"")).thenReturn("127.0.0.1")
            Config.configObject = jsonConfig
            IPFilterHandler.reloadCache()

            val response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(403,response.statusCode())
            }
        }
    }

    @Test
    fun testIpFilterBlackNotInclude(testContext: VertxTestContext){
        testContext.execute {
            //启用IP白名单,包括自己
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
            Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(true)

            Mockito.`when`(jsonConfig.getString(IPFilterHandler.BLACK_LIST_VALUES)).thenReturn("127.0.0.2")
            Config.configObject = jsonConfig
            IPFilterHandler.reloadCache()

            val response = webClient.get(port, host,"/v1/users").send().await()
            testContext.verify {
                Assertions.assertEquals(200,response.statusCode())
            }
        }

    }

}