package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ISVAuthCodeTest : AbstractTest() {

    @Test
    fun testQueryPermanentAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                try {
                    ISVAuthCode.queryPermanentAuthCode(suiteId = created.suiteId,clientType = created.clientType,orgId = created.orgId).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }

                created.permanentAuthCode = randomString()

                val permanent = created.toPermanent().await()
                testContext.verify { Assertions.assertNotNull(permanent) }

                val query = ISVAuthCode.queryPermanentAuthCode(suiteId = permanent.suiteId,clientType = permanent.clientType,orgId = permanent.orgId).await()
                testContext.verify { Assertions.assertNotNull(query) }

                val notExists = ISVAuthCode.queryPermanentAuthCode(suiteId = UUID.randomUUID().toString(),clientType = permanent.clientType,orgId = permanent.orgId).await()
                testContext.verify { Assertions.assertNull(notExists) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryTemporaryAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                val temporary = ISVAuthCode.queryTemporaryAuthCode(suiteId = created.suiteId,orgId = created.orgId,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify {
                    Assertions.assertNotNull(temporary)
                }

                val noExists = ISVAuthCode.queryTemporaryAuthCode(suiteId = UUID.randomUUID().toString(),orgId = created.orgId,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify { Assertions.assertNull(noExists) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                val query = ISVAuthCode.queryAuthCode(suiteId = created.suiteId,clientType = created.clientType,orgId = created.orgId).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                try {
                    ISVAuthCode.queryAuthCode(suiteId = UUID.randomUUID().toString(),clientType = ISVClientType.WorkPlusISV,orgId = created.orgId).await()
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
    fun testToPermanent(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                created.permanentAuthCode = randomString()

                val permanent = created.toPermanent().await()

                testContext.verify {
                    Assertions.assertNotNull(permanent)
                }

                try {
                    created.permanentAuthCode = randomIDString.randomString(128)
                    created.toPermanent().await()
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
    fun testCreateISVAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                try {
                    val errorISVAuthCode = randomISVAuthCode()
                    errorISVAuthCode.suiteId = randomIDString.randomString(128)
                    errorISVAuthCode.createTemporaryAuth().await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }

    private fun randomISVAuthCode():ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgId = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }


}