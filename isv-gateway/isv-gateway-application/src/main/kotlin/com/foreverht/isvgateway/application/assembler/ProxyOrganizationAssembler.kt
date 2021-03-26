package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.domain.ProxyOrganization

fun toOrganizationDTO(proxyOrganization: ProxyOrganization):OrganizationDTO{
    return OrganizationDTO(
        orgId = proxyOrganization.orgId,
        orgCode = proxyOrganization.orgCode,
        domainId = proxyOrganization.authCode.domainId,
        path = proxyOrganization.path,
        name = proxyOrganization.orgId
    )
}