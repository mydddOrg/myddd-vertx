package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVAuthStatus
import com.foreverht.isvgateway.domain.ISVClientType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ISVAuthCodeAssemblerTest : AbstractTest(){

    @Test
    fun testToISVAuthCode(){
        val isvAuthCode = toISVAuthCode(randomISVAuthCodeDTO())
        Assertions.assertNotNull(isvAuthCode)
    }

    @Test
    fun testToISVAuthCodeDTO(){
        val isvAuthCodeDTO = toISVAuthDTO(randomISVAuthCode())
        Assertions.assertNotNull(isvAuthCodeDTO)
    }

    private fun randomISVAuthCode(): ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgId = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }

    private fun randomISVAuthCodeDTO():ISVAuthCodeDTO{
        return ISVAuthCodeDTO(
            suiteId = randomIDString.randomString(),
            clientType = ISVClientType.WorkPlusISV.toString(),
            authStatus = ISVAuthStatus.Temporary.toString(),
            domainId = randomIDString.randomString(),
            orgId = randomIDString.randomString(),
            temporaryAuthCode = randomIDString.randomString(),
            permanentAuthCode = randomIDString.randomString()
        )
    }
}