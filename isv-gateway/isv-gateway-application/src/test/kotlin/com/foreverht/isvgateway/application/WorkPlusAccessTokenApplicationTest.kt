package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.application.workplus.AbstractWorkPlusTest
import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.RequestTokenDTO
import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class WorkPlusAccessTokenApplicationTest : AbstractWorkPlusTest() {

    @Test
    fun testInstance(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(AccessTokenApplication::class.java))
        }
        testContext.completeNow()
    }

    @Test
    fun testRequestToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val tokenResponse = webClient.postAbs("$api/token")
                    .sendJsonObject(requestTokenJsonObject())
                    .await()


                testContext.verify {
                    logger.debug(tokenResponse.bodyAsString())
                    Assertions.assertEquals(200,tokenResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testQueryISVAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val requestTokenDTO = RequestTokenDTO(clientId = isvClientId, clientSecret = clientSecret,
                    domainId = domainId,orgCode = ownerId)

                val tokenDTO = accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(tokenDTO) }

                val isvClientDTO = accessTokenApplication.queryClientByAccessToken(isvAccessToken = tokenDTO.accessToken)
                testContext.verify {
                    Assertions.assertNotNull(isvClientDTO)
                }

                try {
                    accessTokenApplication.queryClientByAccessToken(isvAccessToken = randomString()).await()
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
    fun testRequestAccessTokenByClientId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    val requestTokenDTO = RequestTokenDTO(clientId = randomString(), clientSecret = clientSecret,
                        domainId = domainId,orgCode = ownerId)
                    accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val requestTokenDTO = RequestTokenDTO(clientId = isvClientId, clientSecret = clientSecret,
                    domainId = domainId,orgCode = ownerId)

                val accessToken = accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(accessToken) }

                val isvClientToken = ISVClientToken.queryByToken(accessToken.accessToken).await()
                testContext.verify {
                    Assertions.assertNotNull(isvClientToken)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun requestTokenJsonObject():JsonObject {
        return JsonObject()
            .put("grant_type","client_credentials")
            .put("scope","app")
            .put("domain_id", domainId)
            .put("org_id",ownerId)
            .put("client_id",clientId)
            .put("client_secret",clientSecret)
    }


}