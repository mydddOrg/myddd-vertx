package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
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

class OrganizationApplicationWorkWeiXinTest:AbstractWorkWeiXinTest() {

    private val weiXinSyncDataApplication by lazy { InstanceFactory.getInstance(WeiXinSyncDataApplication::class.java) }
    private val organizationApplication by lazy { InstanceFactory.getInstance(OrganizationApplication::class.java,WORK_WEI_XIN) }

    @Test
    fun testQueryOrganizationEmployees(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val employeeList = organizationApplication.queryOrganizationEmployees(OrgPageQueryDTO(
                    accessToken = isvAccessToken,
                    orgCode = ORG_CODE,
                    orgId = "1"
                )).await()

                testContext.verify {
                    Assertions.assertNotNull(employeeList)
                }

                try {
                    organizationApplication.queryOrganizationEmployees(OrgPageQueryDTO(
                        accessToken = randomString(),
                        orgCode = ORG_CODE,
                        orgId = "1"
                    )).await()
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
    fun testQueryChildrenOrganizations(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val subOrganizations = organizationApplication.queryChildrenOrganizations(OrgPageQueryDTO(
                    accessToken = isvAccessToken,
                    orgCode = ORG_CODE,
                    orgId = "1"
                )).await()

                testContext.verify {
                    Assertions.assertNotNull(subOrganizations)
                    Assertions.assertTrue(subOrganizations.isNotEmpty())
                }

                try {
                    organizationApplication.queryChildrenOrganizations(OrgPageQueryDTO(
                        accessToken = randomString(),
                        orgCode = ORG_CODE,
                        orgId = "1"
                    )).await()

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
    fun testQueryOrganizationById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val rootOrganizationDTO = organizationApplication.queryOrganizationById(isvAccessToken = isvAccessToken,orgCode = ORG_CODE).await()
                testContext.verify { Assertions.assertNotNull(rootOrganizationDTO) }

                val organizationDTO = organizationApplication.queryOrganizationById(isvAccessToken = isvAccessToken,orgCode = ORG_CODE,orgId = "2").await()
                testContext.verify { Assertions.assertNotNull(organizationDTO) }

                try {
                    organizationApplication.queryOrganizationById(isvAccessToken = isvAccessToken,orgCode = ORG_CODE,orgId = randomString()).await()
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

    @BeforeEach
    fun prepareData(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                syncAllData().await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun syncAllData():Future<Unit>{
        return try {
            val authCode = ISVAuthCode.queryAuthCode(suiteId = createdAuthCode.suiteId,domainId = createdAuthCode.domainId,orgCode = createdAuthCode.orgCode,clientType = createdAuthCode.clientType).await()
            weiXinSyncDataApplication.syncAllData(clientId = isvWorkWeiXinClientId,isvAuthCode = authCode!!).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}