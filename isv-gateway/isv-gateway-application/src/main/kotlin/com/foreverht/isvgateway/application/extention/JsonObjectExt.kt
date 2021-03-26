package com.foreverht.isvgateway.application.extention

import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ProxyEmpOrgRelation
import com.foreverht.isvgateway.domain.ProxyEmployee
import com.foreverht.isvgateway.domain.ProxyOrganization
import io.vertx.core.json.JsonObject

fun JsonObject.toProxyOrganization(authCode: ISVAuthCode):ProxyOrganization {
    val organization = ProxyOrganization()
    organization.authCode = authCode
    organization.orgCode = authCode.orgCode
    organization.orgId = this.getString("id")
    organization.parentOrgId = this.getString("parentid")
    organization.name = this.getString("name")
    return organization
}

fun JsonObject.toProxyEmployee(authCode: ISVAuthCode,organizationMap:Map<String,ProxyOrganization>):ProxyEmployee {
    val proxyEmployee = ProxyEmployee()
    proxyEmployee.authCode = authCode
    proxyEmployee.userId = this.getString("userid")
    proxyEmployee.name = this.getString("name")
    proxyEmployee.avatar = this.getString("avatar")
    val mainOrganization = this.getString("main_department")
    val organizations = this.getJsonArray("department")
    val proxyEmpOrgRelationList = mutableListOf<ProxyEmpOrgRelation>()
    organizations.forEach {
        val dept = (it as Int).toString()
        val organization = organizationMap[dept]
        requireNotNull(organization)
        val proxyEmpOrgRelation = ProxyEmpOrgRelation.createInstance(employee = proxyEmployee,organization = organization,isMain = (mainOrganization == dept))
        proxyEmpOrgRelationList.add(proxyEmpOrgRelation)
    }
    proxyEmployee.relations = proxyEmpOrgRelationList
    return proxyEmployee
}