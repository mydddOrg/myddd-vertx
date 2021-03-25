package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractW6SBossTest
import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.dto.RequestTokenDTO
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory


class WorkPlusISVAccessTokenApplicationTest : AbstractW6SBossTest() {

    private val w6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }
    private val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java) }


    companion object {
        private const val ORG_CODE = "2975ff5f83a34f458280fd25fbd3a356"
        private const val DOMAIN_ID = "workplus"
    }

    @Test
    fun testRequestAccessToken(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    with(w6SBossApplication) { requestApiAccessToken(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await() }
                }catch (t:Throwable){
                    Assertions.assertNotNull(t)
                }

                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                }

                val requestTokenDTO = RequestTokenDTO(clientId = isvClientId, clientSecret = isvClientSecret,
                    domainId = DOMAIN_ID,orgCode = ORG_CODE
                )

                val tokenDTO = accessTokenApplication.requestAccessToken(requestTokenDTO = requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(tokenDTO) }

                val query = accessTokenApplication.queryClientByAccessToken(isvAccessToken = tokenDTO.accessToken).await()
                testContext.verify { Assertions.assertNotNull(query) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}