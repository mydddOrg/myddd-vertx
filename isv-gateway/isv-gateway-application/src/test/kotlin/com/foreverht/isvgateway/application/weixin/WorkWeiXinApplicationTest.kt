package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MediaApplication
import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.Media

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WorkWeiXinApplicationTest:AbstractWorkWeiXinTest() {

    private val workWeiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }
    private val mediaApplication by lazy { InstanceFactory.getInstance(MediaApplication::class.java, WORK_WEI_XIN) }

    @Test
    @Order(7)
    fun testQueryAgentId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                saveAuthCodeToLocal(WebClient.create(vertx)).await()

                val clientToken = workWeiXinApplication.requestCorpAccessToken(clientId = isvWorkWeiXinClientId,corpId = "ww6dc4e6c2cbfbb62c").await()

                val corpAccessToken = clientToken.extra.accessToken()

                val agentId = workWeiXinApplication.queryAgentId(corpAccessToken).await()

                testContext.verify { Assertions.assertNotNull(agentId) }

                try {
                    workWeiXinApplication.queryAgentId(randomString()).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }


            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    @Order(6)
    fun testRequestCorpAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                saveAuthCodeToLocal(WebClient.create(vertx)).await()

                val corpAccessToken = workWeiXinApplication.requestCorpAccessToken(clientId = isvWorkWeiXinClientId,corpId = "ww6dc4e6c2cbfbb62c").await()

                testContext.verify {
                    Assertions.assertNotNull(corpAccessToken)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(5)
    fun testUploadResourceToWeiXinMedia(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try{

                val path = MediaApplicationWorkWeiXinTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
                val mediaId = mediaApplication.uploadFile(isvAccessToken = isvAccessToken,path = path).await()
                testContext.verify {
                    Assertions.assertNotNull(mediaId)
                }

                val media = Media.queryMediaById(mediaId = mediaId).await()
                requireNotNull(media)

                val isvClientToken = ISVClientToken.queryByToken(token = isvAccessToken).await()
                requireNotNull(isvClientToken)

                val weiXinMediaId = workWeiXinApplication.uploadResourceToWeiXinTmpMedia(mediaId = media.mediaId,corpAccessToken = isvClientToken.extra.accessToken()).await()
                testContext.verify {
                    logger.debug(weiXinMediaId)
                    Assertions.assertNotNull(weiXinMediaId)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    @Order(4)
    fun testSetSessionInfo(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                workWeiXinApplication.setSessionInfo(clientId = isvWorkWeiXinClientId).await()
                workWeiXinApplication.setSessionInfo(clientId = isvWorkWeiXinClientId,productionMode = true).await()

                try {
                    workWeiXinApplication.setSessionInfo(clientId = randomString()).await()
                    testContext.failNow("不可能到这，ID不存在")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(3)
    fun testRequestPreAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val preAuthCode = workWeiXinApplication.requestPreAuthCode(clientId = isvWorkWeiXinClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(preAuthCode)
                }

                try {
                    workWeiXinApplication.requestPreAuthCode(clientId = randomString()).await()
                    testContext.failNow("不可能到这，ID不存在")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(2)
    fun testRequestSuiteAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClient = workWeiXinApplication.requestSuiteAccessToken(clientId = isvWorkWeiXinClientId).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClient.clientAuthExtra)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @Test
    @Order(1)
    fun testSuiteTicketExists(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                testContext.verify {
                    Assertions.assertNotNull(isvWorkWeiXinClientId)
                }

                val suiteTicket = isvSuiteTicketApplication.querySuiteTicket(suiteId = "wx2547800152da0539",clientType = "WorkWeiXin").await()
                testContext.verify {
                    Assertions.assertNotNull(suiteTicket)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }
}