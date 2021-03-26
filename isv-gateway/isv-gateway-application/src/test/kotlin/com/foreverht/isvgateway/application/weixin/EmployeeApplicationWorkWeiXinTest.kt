package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.EmployeeApplication
import com.foreverht.isvgateway.domain.ISVAuthCode
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class EmployeeApplicationWorkWeiXinTest:AbstractWorkWeiXinTest() {

    private val weiXinSyncDataApplication by lazy { InstanceFactory.getInstance(WeiXinSyncDataApplication::class.java) }
    private val employeeApplication by lazy { InstanceFactory.getInstance(EmployeeApplication::class.java, WORK_WEI_XIN) }

    @Test
    fun testSearchEmployees(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val list = employeeApplication.searchEmployees(
                    isvAccessToken = isvAccessToken,
                    orgCode = ORG_CODE,
                    query = "Liu"
                ).await()

                testContext.verify {
                    Assertions.assertNotNull(list)
                    Assertions.assertTrue(list.isNotEmpty())
                }

                try {
                    employeeApplication.searchEmployees(
                        isvAccessToken = randomString(),
                        orgCode = randomString(),
                        query = "Liu"
                    ).await()
                    testContext.failNow("不可能到这")
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
    fun testBatchQueryEmployeeByIds(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val list = employeeApplication.batchQueryEmployeeByIds(
                    isvAccessToken = isvAccessToken,
                    orgCode = ORG_CODE,
                    userIdList = arrayListOf("LiuLin","yunjian_a")
                ).await()

                testContext.verify {
                    Assertions.assertNotNull(list)
                    Assertions.assertTrue(list.isNotEmpty())
                }

                try {
                    employeeApplication.batchQueryEmployeeByIds(
                        isvAccessToken = randomString(),
                        orgCode = ORG_CODE,
                        userIdList = arrayListOf("LiuLin","yunjian_a")
                    ).await()
                    testContext.failNow("不可能到这")
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
    fun testQueryEmployeeById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                try {
                    employeeApplication.queryEmployeeById(isvAccessToken = isvAccessToken, userId = randomString(),orgCode = ORG_CODE).await()
                    testContext.failNow("不可能到这")
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                val employee = employeeApplication.queryEmployeeById(isvAccessToken = isvAccessToken, userId = "LiuLin",orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(employee)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @BeforeEach
    fun prepareData(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                syncAllData().await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun syncAllData(): Future<Unit> {
        return try {
            val authCode = ISVAuthCode.queryAuthCode(suiteId = createdAuthCode.suiteId,domainId = createdAuthCode.domainId,orgCode = createdAuthCode.orgCode,clientType = createdAuthCode.clientType).await()
            weiXinSyncDataApplication.syncAllData(clientId = isvWorkWeiXinClientId,isvAuthCode = authCode!!).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}