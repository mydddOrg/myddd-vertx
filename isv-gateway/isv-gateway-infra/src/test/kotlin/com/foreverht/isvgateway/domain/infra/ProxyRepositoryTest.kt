package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.ProxyEmpOrgRelation
import com.foreverht.isvgateway.domain.ProxyEmployee
import com.foreverht.isvgateway.domain.ProxyOrganization
import com.foreverht.isvgateway.domain.ProxyRepository
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class ProxyRepositoryTest:AbstractTest() {

    private val proxyRepository by lazy { InstanceFactory.getInstance(ProxyRepository::class.java) }


    @Test
    fun tstSyncEmployeeList(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createAuth = randomISVAuthCode().createTemporaryAuth().await()
                val organizationList = mutableListOf<ProxyOrganization>()
                for(i in 1..20){
                    organizationList.add(randomOrganization(createAuth))
                }
                proxyRepository.syncOrganizationList(createAuth.id,organizationList).await()

                val queryList = ProxyOrganization.queryOrganizations(authCodeId = createAuth.id).await()
                var employeeList = mutableListOf<ProxyEmployee>()
                for(i in 1..10){
                    val employee = randomEmployee(createAuth)
                    employee.relations = arrayListOf(ProxyEmpOrgRelation.createInstance(employee, queryList[i]))

                    employeeList.add(employee)
                }

                proxyRepository.syncEmployeeList(isvAuthCodeId = createAuth.id,employeeList).await()

                var query = ProxyEmployee.queryByAuthCode(authCodeId = createAuth.id).await()
                testContext.verify {
                    Assertions.assertEquals(10,query.size)
                }

                //改变数据
                employeeList = mutableListOf()
                for(i in 1..15){
                    val employee = randomEmployee(createAuth)
                    employee.relations = arrayListOf(ProxyEmpOrgRelation.createInstance(employee, queryList[i]),ProxyEmpOrgRelation.createInstance(employee, queryList[i+1]))

                    employeeList.add(employee)
                }

                proxyRepository.syncEmployeeList(isvAuthCodeId = createAuth.id,employeeList).await()

                query = ProxyEmployee.queryByAuthCode(authCodeId = createAuth.id).await()
                testContext.verify {
                    Assertions.assertEquals(15,query.size)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testSyncOrganizationList(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val createAuth = randomISVAuthCode().createTemporaryAuth().await()

                var organizationList = mutableListOf<ProxyOrganization>()

                for(i in 1..20){
                    organizationList.add(randomOrganization(createAuth))
                }

                proxyRepository.syncOrganizationList(createAuth.id,organizationList).await()

                var query = ProxyOrganization.queryOrganizations(authCodeId = createAuth.id).await()
                testContext.verify {
                    Assertions.assertEquals(20,query.size)
                }


                organizationList = mutableListOf()
                for(i in 1..25){
                    organizationList.add(randomOrganization(createAuth))
                }
                proxyRepository.syncOrganizationList(createAuth.id,organizationList).await()
                query = ProxyOrganization.queryOrganizations(authCodeId = createAuth.id).await()
                testContext.verify {
                    Assertions.assertEquals(25,query.size)
                }

            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }

    }

}