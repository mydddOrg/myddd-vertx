package org.myddd.vertx.oauth2.application

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import java.util.*

class TestOAuth2ClientApplicationJPA : AbstractTest() {

    private val oAuth2ClientApplication by lazy {InstanceFactory.getInstance(OAuth2ClientApplicationJPA::class.java)}

    @Test
    fun test(){
        Assertions.assertNotNull(oAuth2ClientApplication)
    }

    @Test
    fun testCreateClient(testContext: VertxTestContext){
        testContext.execute {
            val clientId = UUID.randomUUID().toString()
            val clientDto = OAuth2ClientDTO(clientId = clientId,name = "测试应用",description = "这是一个测试应用，没有任何其它意义")
            val created = oAuth2ClientApplication.createClient(clientDto).await()
            testContext.verify {
                Assertions.assertNotNull(created.id)
                Assertions.assertNotNull(created.clientSecret)
            }

            val notValidClientDTO = OAuth2ClientDTO(clientId = clientId,name = "测试应用",description = "这是一个测试应用，没有任何其它意义",clientSecret = UUID.randomUUID().toString(),id = 1,version =1)

            testContext.assertThrow(Exception::class.java){
                oAuth2ClientApplication.createClient(notValidClientDTO).await()
            }
        }
    }


    @Test
    fun testQueryClient(testContext: VertxTestContext){
        testContext.execute {
            val clientDto = OAuth2ClientDTO(clientId = UUID.randomUUID().toString(),name = "测试应用",description = "这是一个测试应用，没有任何其它意义")
            val created = oAuth2ClientApplication.createClient(clientDto).await()
            testContext.verify {
                Assertions.assertNotNull(created.id)
                Assertions.assertNotNull(created.clientSecret)
            }

            val queryClient = oAuth2ClientApplication.queryClient(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(queryClient)
            }

            val notExistsQuery = oAuth2ClientApplication.queryClient(UUID.randomUUID().toString()).await()
            testContext.verify {
                Assertions.assertNull(notExistsQuery)
            }
        }
    }

    @Test
    fun testResetClientSecret(testContext: VertxTestContext){
        testContext.execute {
            try {
                oAuth2ClientApplication.resetClientSecret(UUID.randomUUID().toString()).await()
                testContext.failNow("不可能重置一个不存在的CLIENT ID")
            }catch (e:Exception){
                testContext.verify { Assertions.assertNotNull(e) }
            }

            val clientDto = OAuth2ClientDTO(clientId = UUID.randomUUID().toString(),name = "测试应用",description = "这是一个测试应用，没有任何其它意义")
            val created = oAuth2ClientApplication.createClient(clientDto).await()
            testContext.verify {
                Assertions.assertNotNull(created.id)
                Assertions.assertNotNull(created.clientSecret)
            }

            val reset = oAuth2ClientApplication.resetClientSecret(created.clientId).await()
            testContext.verify { Assertions.assertNotNull(reset) }

            val queryReset = oAuth2ClientApplication.queryClient(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(queryReset)
                Assertions.assertNotEquals(queryReset!!.clientSecret,created.clientSecret)
            }
        }

    }

    @Test
    fun testEnableAndDisableClient(testContext: VertxTestContext){
        testContext.execute {
            testContext.assertThrow(Exception::class.java){
                oAuth2ClientApplication.enableClient(UUID.randomUUID().toString()).await()
            }

            val clientDto = OAuth2ClientDTO(clientId = UUID.randomUUID().toString(),name = "测试应用",description = "这是一个测试应用，没有任何其它意义")
            val created = oAuth2ClientApplication.createClient(clientDto).await()
            testContext.verify {
                Assertions.assertNotNull(created.id)
                Assertions.assertNotNull(created.clientSecret)
                Assertions.assertFalse(created.disabled)
            }


            oAuth2ClientApplication.disableClient(created.clientId).await()
            val queryDisabled = oAuth2ClientApplication.queryClient(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(queryDisabled)
                Assertions.assertTrue(queryDisabled!!.disabled)
            }

            oAuth2ClientApplication.enableClient(created.clientId).await()
            val queryEnabled = oAuth2ClientApplication.queryClient(created.clientId).await()
            testContext.verify {
                Assertions.assertNotNull(queryEnabled)
                Assertions.assertFalse(queryEnabled!!.disabled)
            }
        }
    }
}