package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusISVDTO
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class ISVClientApplicationTest : AbstractTest() {

    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }



    @Test
    fun testInstanceFactory(vertx: Vertx,testContext: VertxTestContext){
        testContext.verify {
            Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java) )
        }
        testContext.completeNow()
    }

    @Test

    fun testCreateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientDTO = randomISVClient()
                val created = isvClientApplication.createISVClient(isvClientDTO).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.clientId)
                    Assertions.assertNotNull(created.clientSecret)
                }

                val w6sISVCreated = isvClientApplication.createISVClient(randomISVClient()).await()
                testContext.verify {
                    Assertions.assertNotNull(w6sISVCreated)
                    Assertions.assertNotNull(w6sISVCreated.clientId)
                    Assertions.assertNotNull(w6sISVCreated.clientSecret)
                }
                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }

        }
    }

    @Test
    fun testQueryISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientDTO = randomISVClient()
                val created = isvClientApplication.createISVClient(isvClientDTO).await()

                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.clientId)
                    Assertions.assertNotNull(created.clientSecret)
                }


                val queryClient = isvClientApplication.queryClientByClientId(created.clientId!!)
                testContext.verify {
                    Assertions.assertNotNull(queryClient)
                }

                val notExistsQuery = isvClientApplication.queryClientByClientId(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(notExistsQuery)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val isvClientDTO = randomISVClient()
                val created = isvClientApplication.createISVClient(isvClientDTO).await()

                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertNotNull(created.clientId)
                    Assertions.assertNotNull(created.clientSecret)
                }

                created.clientName = UUID.randomUUID().toString()
                (created.extra as ISVClientExtraForWorkPlusDTO).appSecret = UUID.randomUUID().toString()
                val updated = isvClientApplication.updateISVClient(created).await()

                testContext.verify {
                    Assertions.assertNotNull(updated)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomISVClient() : ISVClientDTO {
        val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
            appKey = UUID.randomUUID().toString(),
            appSecret = UUID.randomUUID().toString(),
            domainId = UUID.randomUUID().toString(),
            api = UUID.randomUUID().toString(),
            ownerId = randomIDString.randomString()
        )

        return ISVClientDTO(clientName = UUID.randomUUID().toString(),extra = isvClientExtraDTO,callback = UUID.randomUUID().toString())
    }

    private fun randomW6SISVClient() : ISVClientDTO {
        val isvClientExtraDTO = ISVClientExtraForWorkPlusISVDTO(
            suiteKey = randomString(),
            suiteSecret = randomString(),
            vendorKey = randomString(),
            token = randomString(),
            encryptSecret = randomString(),
            isvApi = randomString(),
            appId = randomString()
        )

        return ISVClientDTO(clientName = UUID.randomUUID().toString(),extra = isvClientExtraDTO,callback = UUID.randomUUID().toString())
    }
}