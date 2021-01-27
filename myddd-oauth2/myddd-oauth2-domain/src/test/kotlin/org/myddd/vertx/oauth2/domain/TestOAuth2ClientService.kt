package org.myddd.vertx.oauth2.domain

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class TestOAuth2ClientService : AbstractTest() {

    private val oAuth2ClientService:OAuth2ClientService by lazy {InstanceFactory.getInstance(OAuth2ClientService::class.java)}

    @Test
    fun testQueryClientByClientId(testContext: VertxTestContext){
        executeWithTryCatch(testContext){

            GlobalScope.launch {
                val client = OAuth2Client()
                client.name = UUID.randomUUID().toString()
                val created = client.createClient().await()

                var query = oAuth2ClientService.queryClientByClientId(created.clientId).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                query = oAuth2ClientService.queryClientByClientId(UUID.randomUUID().toString()).await()

                testContext.verify {
                    Assertions.assertNull(query)
                }

                testContext.completeNow()
            }

        }

    }

}