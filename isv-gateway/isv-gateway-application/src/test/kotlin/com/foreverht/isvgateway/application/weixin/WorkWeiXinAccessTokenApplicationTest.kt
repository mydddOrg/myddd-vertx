package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.RequestTokenDTO
import com.foreverht.isvgateway.application.AccessTokenApplicationImpl
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class WorkWeiXinAccessTokenApplicationTest : AbstractWorkWeiXinTest() {


    private val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplicationImpl::class.java) }


    @Test
    fun testRequestAccessToken(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val requestTokenDTO = RequestTokenDTO(clientId = isvWorkWeiXinClientId, clientSecret = isvWorkWeiXinClientSecret,orgCode = "ww6dc4e6c2cbfbb62c")

                try {
                    accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                    testContext.failNow("不可能到这,没有企业永久授权码")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                saveAuthCodeToLocal(webClient).await()

                val accessToken = accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(accessToken) }

                val accessTokenRepeat = accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(accessTokenRepeat) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}