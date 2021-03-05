package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ISVClientExtraForWorkPlusISVTest {

    @Test
    fun testCreateInstance(){
        val extraForWorkPlusISV = ISVClientExtraForWorkPlusISV()
        extraForWorkPlusISV.appId = UUID.randomUUID().toString()
        extraForWorkPlusISV.suiteKey = UUID.randomUUID().toString()
        extraForWorkPlusISV.suiteSecret = UUID.randomUUID().toString()
        extraForWorkPlusISV.vendorkey = UUID.randomUUID().toString()

        Assertions.assertEquals(ISVClientType.WorkPlusISV,extraForWorkPlusISV.clientType)
        Assertions.assertNotNull(extraForWorkPlusISV.appId)
        Assertions.assertNotNull(extraForWorkPlusISV.suiteKey)
        Assertions.assertNotNull(extraForWorkPlusISV.suiteSecret)
        Assertions.assertNotNull(extraForWorkPlusISV.vendorkey)
    }
}