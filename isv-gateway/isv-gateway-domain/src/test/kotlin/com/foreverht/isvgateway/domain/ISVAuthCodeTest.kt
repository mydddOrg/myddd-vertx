package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForISV
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
    fun testSaveAuthCodeExtra(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                created.id = 0
                created.temporaryAuthCode = randomString()
                created.createTemporaryAuth().await()

                try {
                    ISVAuthCode.queryPermanentAuthCode(suiteId = created.suiteId,clientType = created.clientType,domainId = created.domainId,orgCode = created.orgCode).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }

                created.permanentAuthCode = randomString()

                val permanent = created.toPermanent().await()
                testContext.verify { Assertions.assertNotNull(permanent) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testCreateISVAuthCodeExtra(){
        val extra = ISVClientAuthExtraForISV.createInstance(randomString(),System.currentTimeMillis())
        Assertions.assertNotNull(extra)
    }

    @Test
    fun testQueryPermanentAuthCode(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomISVAuthCode().createTemporaryAuth().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }

                created.id = 0
                created.temporaryAuthCode = randomString()
                created.createTemporaryAuth().await()

                try {
                    ISVAuthCode.queryPermanentAuthCode(suiteId = created.suiteId,clientType = created.clientType,domainId = created.domainId,orgCode = created.orgCode).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }

                created.permanentAuthCode = randomString()

                val permanent = created.toPermanent().await()
                testContext.verify { Assertions.assertNotNull(permanent) }

                val query = ISVAuthCode.queryPermanentAuthCode(suiteId = permanent.suiteId,clientType = permanent.clientType,domainId = created.domainId,orgCode = permanent.orgCode).await()
                testContext.verify { Assertions.assertNotNull(query) }

                val notExists = ISVAuthCode.queryPermanentAuthCode(suiteId = UUID.randomUUID().toString(),clientType = permanent.clientType,domainId = created.domainId,orgCode = permanent.orgCode).await()
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

                val temporary = ISVAuthCode.queryTemporaryAuthCode(suiteId = created.suiteId,domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV).await()
                testContext.verify {
                    Assertions.assertNotNull(temporary)
                }

                val noExists = ISVAuthCode.queryTemporaryAuthCode(suiteId = UUID.randomUUID().toString(),domainId = created.domainId,orgCode = created.orgCode,clientType = ISVClientType.WorkPlusISV).await()
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

                val query = ISVAuthCode.queryAuthCode(suiteId = created.suiteId,clientType = created.clientType,domainId = created.domainId,orgCode = created.orgCode).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                try {
                    ISVAuthCode.queryAuthCode(suiteId = UUID.randomUUID().toString(),clientType = ISVClientType.WorkPlusISV,domainId = created.domainId,orgCode = created.orgCode).await()
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

    private fun randomAuthExtra():ISVClientAuthExtra{
        val extra = ISVClientAuthExtraForISV()
        extra.expireTime = System.currentTimeMillis()
        extra.accessToken = randomString()
        return extra
    }

    private fun randomISVAuthCode():ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgCode = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }


}