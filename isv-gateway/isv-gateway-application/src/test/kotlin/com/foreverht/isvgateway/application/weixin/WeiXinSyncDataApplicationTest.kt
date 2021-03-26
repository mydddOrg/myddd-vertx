package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ProxyEmployee
import com.foreverht.isvgateway.domain.ProxyOrganization
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.myddd.vertx.ioc.InstanceFactory

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WeiXinSyncDataApplicationTest:AbstractWorkWeiXinTest() {

    private val weiXinSyncDataApplication by lazy { InstanceFactory.getInstance(WeiXinSyncDataApplication::class.java) }

    private val workWeiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }

    @Test
    @Order(3)
    fun testSyncAllData(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                saveAuthCodeToLocal(WebClient.create(vertx)).await()
                val isvAuthCode = ISVAuthCode.queryAuthCode(suiteId = createdAuthCode.suiteId,domainId = createdAuthCode.domainId,orgCode = createdAuthCode.orgCode,clientType = createdAuthCode.clientType).await()

                weiXinSyncDataApplication.syncAllData(clientId = isvWorkWeiXinClientId,isvAuthCode = isvAuthCode!!).await()
                val employeeList = ProxyEmployee.queryByAuthCode(authCodeId = isvAuthCode.id).await()
                testContext.verify {
                    Assertions.assertTrue(employeeList.isNotEmpty())
                }

                val organizationList = ProxyOrganization.queryOrganizations(authCodeId = isvAuthCode.id).await()
                testContext.verify {
                    Assertions.assertTrue(organizationList.isNotEmpty())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(2)
    fun testSyncEmployeeData(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                saveAuthCodeToLocal(WebClient.create(vertx)).await()
                val tokenDTO = workWeiXinApplication.requestCorpAccessToken(clientId = isvWorkWeiXinClientId,corpId = "ww6dc4e6c2cbfbb62c").await()
                testContext.verify { Assertions.assertNotNull(tokenDTO) }
                val isvAuthCode = ISVAuthCode.queryAuthCode(suiteId = createdAuthCode.suiteId,domainId = createdAuthCode.domainId,orgCode = createdAuthCode.orgCode,clientType = createdAuthCode.clientType).await()

                weiXinSyncDataApplication.syncOrganizationData(corpAccessToken = tokenDTO.accessToken(),isvAuthCode = isvAuthCode!!).await()

                weiXinSyncDataApplication.syncEmployeeData(corpAccessToken = tokenDTO.accessToken(),isvAuthCode = isvAuthCode).await()

                val employeeList = ProxyEmployee.queryByAuthCode(authCodeId = isvAuthCode.id).await()
                testContext.verify {
                    Assertions.assertTrue(employeeList.isNotEmpty())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    @Order(1)
    fun testSyncOrganizationData(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                saveAuthCodeToLocal(WebClient.create(vertx)).await()

                val tokenDTO = workWeiXinApplication.requestCorpAccessToken(clientId = isvWorkWeiXinClientId,corpId = "ww6dc4e6c2cbfbb62c").await()
                testContext.verify { Assertions.assertNotNull(tokenDTO) }

                val isvAuthCode = ISVAuthCode.queryAuthCode(suiteId = createdAuthCode.suiteId,domainId = createdAuthCode.domainId,orgCode = createdAuthCode.orgCode,clientType = createdAuthCode.clientType).await()
                val organizationList = weiXinSyncDataApplication.syncOrganizationData(corpAccessToken = tokenDTO.accessToken(),isvAuthCode = isvAuthCode!!).await()
                testContext.verify {
                    organizationList.forEach {
                        logger.debug("${it.orgCode} - ${it.orgId} - ${it.parentOrgId} - ${it.path}")
                    }
                    Assertions.assertTrue(organizationList.isNotEmpty())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }
}