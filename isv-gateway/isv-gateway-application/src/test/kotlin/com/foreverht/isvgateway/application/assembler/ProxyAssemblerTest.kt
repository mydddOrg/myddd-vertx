package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ProxyAssemblerTest {

    @Test
    fun testToEmployeeDTO(){
        val isvAuthCode = randomISVAuthCode()
        val employee = randomEmployee(isvAuthCode)

        val employeeDTO = toEmployeeDTO(employee)
        Assertions.assertNotNull(employeeDTO)
    }

    @Test
    fun testToOrganizationDTO(){
        val isvAuthCode = randomISVAuthCode()
        val organization = randomOrganization(isvAuthCode)

        val organizationDTO = toOrganizationDTO(organization)
        Assertions.assertNotNull(organizationDTO)
    }

    private fun randomISVAuthCode(): ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgCode = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }

    private fun randomEmployee(authCode: ISVAuthCode): ProxyEmployee {
        val employee = ProxyEmployee()
        employee.authCode = authCode
        employee.userId = randomString()
        employee.name = randomString()
        employee.avatar = randomString()
        employee.mobile = randomString()
        employee.email = randomString()
        return employee
    }

    private fun randomOrganization(authCode: ISVAuthCode): ProxyOrganization {
        val organization = ProxyOrganization()
        organization.authCode = authCode
        organization.orgId = randomString()
        organization.orgCode = randomString()
        organization.parentOrgId = randomString()
        organization.path = randomString()
        organization.name = randomString()
        return organization
    }

    private fun randomString():String{
        return UUID.randomUUID().toString()
    }

}