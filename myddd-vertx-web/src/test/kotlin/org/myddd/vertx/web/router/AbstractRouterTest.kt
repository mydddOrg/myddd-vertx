package org.myddd.vertx.web.router

import com.google.inject.Guice
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.web.router.config.GlobalConfig
import org.myddd.vertx.web.router.handler.IPFilterHandler
import java.util.*
import kotlin.Exception

@ExtendWith(VertxExtension::class)
class AbstractRouterTest {

    companion object{

        private val logger = LoggerFactory.getLogger(AbstractRouterTest::class.java)

        private var port = 8080

        private const val host = "127.0.0.1"

        private lateinit var deployId:String

        private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        private val oAuth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }
        private val oAuth2ClientApplication:OAuth2ClientApplication by lazy { InstanceFactory.getInstance(OAuth2ClientApplication::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx:Vertx,testContext: VertxTestContext){

            GlobalScope.launch(vertx.dispatcher()) {
                port = 10000 + Random().nextInt(1000)
                InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(WebGuice(vertx))))
                deployId = vertx.deployVerticle(WebVerticle(port = port)).await()
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
    fun testInstanceFactory(vertx:Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java))
        }
        testContext.completeNow()
    }

    @BeforeEach
    fun disableIpFilter(vertx: Vertx,testContext: VertxTestContext){
        Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
        Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(false)
        GlobalConfig.configObject = jsonConfig
        IPFilterHandler.reloadCache()

        testContext.completeNow()
    }

    @Test
    fun testNotExistsRoute(vertx:Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
    fun testAuthorizationGetRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var response = webClient.get(port,host,"/v1/authorization/users").send().await()
                testContext.verify {
                    Assertions.assertEquals(403,response.statusCode())
                }

                response = webClient.get(port,host,"/v1/authorization/users?accessToken=${UUID.randomUUID()}").send().await()
                testContext.verify {
                    Assertions.assertEquals(403,response.statusCode())
                }

                val clientDto = OAuth2ClientDTO(clientId = UUID.randomUUID().toString(),name = "测试应用",description = "这是一个测试应用，没有任何其它意义")
                val created = oAuth2ClientApplication.createClient(clientDto).await()

                var userDTO = oAuth2Application.requestClientToken(created.clientId, created.clientSecret!!).await()

                testContext.verify {
                    Assertions.assertNotNull(userDTO)
                    Assertions.assertFalse(userDTO!!.expired())
                }

                response = webClient.get(port,host,"/v1/authorization/users?accessToken=${userDTO!!.tokenDTO!!.accessToken}").send().await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testGetRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }


    @Test
    fun testPostRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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

            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testPutRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testPatchRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val userId = UUID.randomUUID().toString()

                var response = webClient.patch(port,host,"/v1/users/$userId")
                    .sendJsonObject(JsonObject().put("name",UUID.randomUUID().toString()))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                    val bodyJson = response.bodyAsJsonObject()

                    Assertions.assertEquals(userId,bodyJson.getString("userId"))
                    Assertions.assertNotNull(bodyJson.getString("name"))
                }

                response = webClient.patch(port,host,"/v1/users/$userId?error=true")
                    .sendJsonObject(JsonObject().put("name",UUID.randomUUID().toString()))
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,response.statusCode())
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testDeleteRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
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
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
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

    private val jsonConfig = Mockito.mock(JsonObject::class.java)

    @Test
    fun testNoIpFilter(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {

            try {
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
                GlobalConfig.configObject = jsonConfig

                testContext.verify {
                    Assertions.assertEquals(false,jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE))
                    Assertions.assertEquals(false,
                        GlobalConfig.getConfig()?.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE))
                }

                IPFilterHandler.reloadCache()

                var response = webClient.get(port, host,"/v1/users").send().await()
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
    fun testIpFilterWhite(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {

            try {
                //启用IP白名单,包括自己
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(true)
                Mockito.`when`(jsonConfig.getString(IPFilterHandler.WHITE_LIST_VALUES)).thenReturn("127.0.0.1")
                GlobalConfig.configObject = jsonConfig

                IPFilterHandler.reloadCache()

                var response = webClient.get(port, host,"/v1/users").send().await()
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
    fun testIpFilterWhiteNotInclude(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {

            try {
                //启用IP白名单,包括自己
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(true)
                Mockito.`when`(jsonConfig.getString(IPFilterHandler.WHITE_LIST_VALUES)).thenReturn("127.0.0.2")
                GlobalConfig.configObject = jsonConfig
                IPFilterHandler.reloadCache()


                var response = webClient.get(port, host,"/v1/users").send().await()
                testContext.verify {
                    Assertions.assertEquals(403,response.statusCode())
                }

            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testIpFilterBlack(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {

            try {
                //启用IP白名单,包括自己
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(true)

                Mockito.`when`(jsonConfig.getString(IPFilterHandler.BLACK_LIST_VALUES)).thenReturn("127.0.0.1")
                GlobalConfig.configObject = jsonConfig
                IPFilterHandler.reloadCache()

                var response = webClient.get(port, host,"/v1/users").send().await()
                testContext.verify {
                    Assertions.assertEquals(403,response.statusCode())
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testIpFilterBlackNotInclude(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {

            try {
                //启用IP白名单,包括自己
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.WHITE_LIST_ENABLE)).thenReturn(false)
                Mockito.`when`(jsonConfig.getBoolean(IPFilterHandler.BLACK_LIST_ENABLE)).thenReturn(true)

                Mockito.`when`(jsonConfig.getString(IPFilterHandler.BLACK_LIST_VALUES)).thenReturn("127.0.0.2")
                GlobalConfig.configObject = jsonConfig
                IPFilterHandler.reloadCache()

                var response = webClient.get(port, host,"/v1/users").send().await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }
            }catch (e:Exception){
                testContext.failNow(e)
            }
            testContext.completeNow()
        }
    }

}