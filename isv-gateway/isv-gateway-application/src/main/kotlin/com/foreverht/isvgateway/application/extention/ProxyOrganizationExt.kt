package com.foreverht.isvgateway.application.extention

import com.foreverht.isvgateway.domain.ProxyOrganization

fun ProxyOrganization.parseAbsolutePath(organizationMap:Map<String,ProxyOrganization>) {
    val pathList:MutableList<String> = mutableListOf(this.orgId)
    var parentId = this.parentOrgId
    while (parentId != "0"){
        pathList.add(parentId)
        val parentOrganization = organizationMap[parentId]
        requireNotNull(parentOrganization)
        parentId = parentOrganization.parentOrgId
    }
    pathList.reverse()
    this.path = pathList.joinToString("/")
}