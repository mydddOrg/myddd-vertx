package com.foreverht.isvgateway.application.isv

import com.foreverht.isvgateway.AbstractW6SBossTest
import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class OrganizationApplicationISVTest : AbstractWorkPlusISVTest() {

    private val organizationApplication: OrganizationApplication by lazy { InstanceFactory.getInstance(OrganizationApplication::class.java,"WorkPlusApp") }

    @Test
    fun testInstance(vertx: Vertx, testContext: VertxTestContext){
        testContext.verify { Assertions.assertNotNull(organizationApplication) }
        testContext.completeNow()
    }

    @Test
    fun testQueryOrganizationById(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val organizationDTO = organizationApplication.queryOrganizationById(orgCode = ORG_CODE,isvAccessToken = isvAccessToken).await()
                testContext.verify { Assertions.assertNotNull(organizationDTO) }

                val subOrganizationDTO = organizationApplication.queryOrganizationById(isvAccessToken = isvAccessToken,orgCode = ORG_CODE,orgId = "aHexITjYkEurKyyxpKMgFh").await()
                testContext.verify { Assertions.assertNotNull(subOrganizationDTO) }

                try {
                    organizationApplication.queryOrganizationById(orgCode = randomIDString.randomString(),isvAccessToken = isvAccessToken).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

                try {
                    organizationApplication.queryOrganizationById(orgCode = ORG_CODE,isvAccessToken = isvAccessToken, orgId = randomIDString.randomString()).await()
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
                var subOrganizationList = organizationApplication.queryChildrenOrganizations(OrgPageQueryDTO(accessToken = isvAccessToken,orgCode = ORG_CODE)).await()
                testContext.verify {
                    Assertions.assertNotNull(subOrganizationList)
                }

                subOrganizationList = organizationApplication.queryChildrenOrganizations(OrgPageQueryDTO(accessToken = isvAccessToken,orgCode = ORG_CODE,orgId = "aHexITjYkEurKyyxpKMgFh")).await()
                testContext.verify {
                    Assertions.assertNotNull(subOrganizationList)
                }

                try {
                    organizationApplication.queryChildrenOrganizations(OrgPageQueryDTO(accessToken = randomIDString.randomString(),orgCode = randomIDString.randomString()))
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
    fun testQueryOrganizationEmployees(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var employees = organizationApplication.queryOrganizationEmployees(OrgPageQueryDTO(accessToken = isvAccessToken,orgCode = ORG_CODE)).await()
                testContext.verify {
                    Assertions.assertNotNull(employees)
                }

                employees = organizationApplication.queryOrganizationEmployees(OrgPageQueryDTO(accessToken = isvAccessToken,orgCode = ORG_CODE,orgId = "aHexITjYkEurKyyxpKMgFh")).await()
                testContext.verify {
                    logger.debug("COUNT:${employees.count()}")
                    Assertions.assertNotNull(employees)
                }

                try {
                    organizationApplication.queryOrganizationEmployees(OrgPageQueryDTO(accessToken = randomIDString.randomString(),orgCode = randomIDString.randomString()))
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }

}