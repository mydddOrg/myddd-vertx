package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVAuthStatus
import com.foreverht.isvgateway.domain.ISVClientType

fun toISVAuthCode(isvAuthCodeDTO: ISVAuthCodeDTO):ISVAuthCode{
    val isvAuthCode = ISVAuthCode()
    isvAuthCode.suiteId = isvAuthCodeDTO.suiteId
    isvAuthCode.clientType = ISVClientType.valueOf(isvAuthCodeDTO.clientType)
    isvAuthCode.authStatus = ISVAuthStatus.valueOf(isvAuthCodeDTO.authStatus)
    isvAuthCode.domainId = isvAuthCodeDTO.domainId
    isvAuthCode.orgId = isvAuthCodeDTO.orgId
    isvAuthCode.temporaryAuthCode = isvAuthCodeDTO.temporaryAuthCode
    isvAuthCode.permanentAuthCode = isvAuthCodeDTO.permanentAuthCode
    return isvAuthCode
}

fun toISVAuthDTO(isvAuthCode: ISVAuthCode):ISVAuthCodeDTO{
    return ISVAuthCodeDTO(
        suiteId = isvAuthCode.suiteId,
        clientType = isvAuthCode.clientType.toString(),
        authStatus = isvAuthCode.authStatus.toString(),
        domainId = isvAuthCode.domainId,
        orgId = isvAuthCode.orgId,
        temporaryAuthCode = isvAuthCode.temporaryAuthCode,
        permanentAuthCode = isvAuthCode.permanentAuthCode
    )
}