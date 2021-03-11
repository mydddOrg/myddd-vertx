package com.foreverht.isvgateway.bootstrap.router

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class OrganizationRouterTest : AbstractISVRouterTest() {


    @Test
    fun testQueryOrganization(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var response = webClient.get(port,host,"/v1/organizations?orgCode=$ownerId&accessToken=$accessToken")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                response = webClient.get(port,host,"/v1/organizations?orgCode=$ownerId&accessToken=$accessToken&orgId=$orgId")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }

                var errorResponse = webClient.get(port,host,"/v1/organizations?orgCode=$ownerId&accessToken=${UUID.randomUUID()}&orgId=$orgId")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(403,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations?orgCode=${UUID.randomUUID()}&accessToken=$accessToken&orgId=$orgId")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                }

                errorResponse = webClient.get(port,host,"/v1/organizations?accessToken=$accessToken&orgId=$orgId")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}