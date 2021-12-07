package org.myddd.vertx.oauth2.infra

import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.domain.OAuth2Client
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import java.util.*

class TestOAuth2ClientRepositoryHibernate : AbstractTest() {

    private val repository:OAuth2ClientRepository by lazy { InstanceFactory.getInstance(OAuth2ClientRepository::class.java)}

    @Test
    fun testQueryClientByClientIdFailed(testContext: VertxTestContext){
        testContext.execute {
            val empty  = repository.queryClientByClientId(UUID.randomUUID().toString()).await()
            testContext.verify {
                Assertions.assertNull(empty)
            }
        }
    }

    @Test
    fun testQueryClientByClientId(testContext: VertxTestContext){
        testContext.execute {
            val client = OAuth2Client()
            client.clientId = UUID.randomUUID().toString()
            client.name = "TEST_CLIENT"
            val created = client.createClient().await()
            val query = repository.queryClientByClientId(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(query)
            }
        }
    }

}