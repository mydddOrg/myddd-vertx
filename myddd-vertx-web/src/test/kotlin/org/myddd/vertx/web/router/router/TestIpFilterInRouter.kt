package org.myddd.vertx.web.router.router

import com.google.inject.Guice
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.AbstractRouterTest
import org.myddd.vertx.web.router.WebGuice
import org.myddd.vertx.web.router.WebVerticle
import org.myddd.vertx.web.router.handler.IPFilterHandler
import java.lang.RuntimeException
import java.util.*

class TestIpFilterInRouter:AbstractRouterTest() {

    val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    val jsonConfig: JsonObject = Mockito.mock(JsonObject::class.java)

    init {
        Mockito.`when`(jsonConfig.getString(any(),any())).thenReturn("")
        Mockito.`when`(jsonConfig.getBoolean(any(),any())).thenReturn(false)
        Mockito.`when`(jsonConfig.getInteger(any(),any())).thenReturn(0)
    }

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

    @BeforeEach
    fun disableIpFilter(vertx: Vertx,testContext: VertxTestContext){
        Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
        Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(false)
        Config.configObject = jsonConfig
        IPFilterHandler.reloadCache()

        testContext.completeNow()
    }

    @Test
    fun testAsyncMock(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            val future = Mockito.mock(Future::class.java)
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

    @Test
    fun testNoIpFilter(vertx: Vertx, testContext: VertxTestContext){
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
    fun testIpFilterWhite(vertx: Vertx, testContext: VertxTestContext){
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
    fun testIpFilterWhiteNotInclude(vertx: Vertx, testContext: VertxTestContext){
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
    fun testIpFilterBlack(vertx: Vertx, testContext: VertxTestContext){
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
    fun testIpFilterBlackNotInclude(vertx: Vertx, testContext: VertxTestContext){
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