package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.domain.ISVAuthStatus
import com.foreverht.isvgateway.domain.ISVClientType
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

class ISVAuthCodeApplicationTest: AbstractTest() {

    private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }

    @Test
    fun testQueryPermanent(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = isvAuthCodeApplication.createTemporaryAuthCode(randomISVAuthCodeDTO()).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertEquals(ISVAuthStatus.Temporary.toString(),created!!.authStatus)
                }


                var noExistPermanent = isvAuthCodeApplication.queryPermanentAuthCode(suiteId = created!!.suiteId,domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV.toString()).await()
                testContext.verify { Assertions.assertNull(noExistPermanent) }

                created.permanentAuthCode = randomString()

                val permanent = isvAuthCodeApplication.toPermanent(created).await()
                testContext.verify { Assertions.assertNotNull(permanent) }

                val queryPermanent = isvAuthCodeApplication.queryPermanentAuthCode(suiteId = permanent!!.suiteId,domainId = created.domainId,orgCode = permanent.orgCode,clientType = ISVClientType.WorkPlusISV.toString()).await()
                testContext.verify { Assertions.assertNotNull(queryPermanent) }

                noExistPermanent = isvAuthCodeApplication.queryPermanentAuthCode(suiteId = UUID.randomUUID().toString(),domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV.toString()).await()
                testContext.verify { Assertions.assertNull(noExistPermanent) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryTemporary(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = isvAuthCodeApplication.createTemporaryAuthCode(randomISVAuthCodeDTO()).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertEquals(ISVAuthStatus.Temporary.toString(),created!!.authStatus)
                }

                val queryTemporary = isvAuthCodeApplication.queryTemporaryAuthCode(suiteId = created!!.suiteId,domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV.toString()).await()
                testContext.verify { Assertions.assertNotNull(queryTemporary) }

                var noExistsAuthCode = isvAuthCodeApplication.queryTemporaryAuthCode(suiteId = UUID.randomUUID().toString(),domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV.toString()).await()

                testContext.verify { Assertions.assertNull(noExistsAuthCode) }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testToPermanent(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = isvAuthCodeApplication.createTemporaryAuthCode(randomISVAuthCodeDTO()).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertEquals(ISVAuthStatus.Temporary.toString(),created!!.authStatus)
                }

                created!!.permanentAuthCode = randomString()

                val permanentDTO = isvAuthCodeApplication.toPermanent(created)
                testContext.verify {
                    Assertions.assertNotNull(permanentDTO)
                }

                try {
                    created.permanentAuthCode = randomIDString.randomString(128)
                    isvAuthCodeApplication.toPermanent(created)
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateTemporaryAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = isvAuthCodeApplication.createTemporaryAuthCode(randomISVAuthCodeDTO()).await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                    Assertions.assertEquals(ISVAuthStatus.Temporary.toString(),created!!.authStatus)
                }

                try {
                    val errorISVAuthCode = ISVAuthCodeDTO(
                        suiteId = randomIDString.randomString(128),
                        clientType = ISVClientType.WorkPlusISV.toString(),
                        authStatus = ISVAuthStatus.Temporary.toString(),
                        domainId = randomIDString.randomString(),
                        orgCode = randomIDString.randomString(),
                        temporaryAuthCode = randomIDString.randomString(),
                        permanentAuthCode = randomIDString.randomString()
                    )
                    isvAuthCodeApplication.createTemporaryAuthCode(errorISVAuthCode).await()

                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomISVAuthCodeDTO(): ISVAuthCodeDTO {
        return ISVAuthCodeDTO(
            suiteId = randomIDString.randomString(),
            clientType = ISVClientType.WorkPlusISV.toString(),
            authStatus = ISVAuthStatus.Temporary.toString(),
            domainId = randomIDString.randomString(),
            orgCode = randomIDString.randomString(),
            temporaryAuthCode = randomIDString.randomString(),
            permanentAuthCode = randomIDString.randomString()
        )
    }

}