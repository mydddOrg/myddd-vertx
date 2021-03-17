package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteTicket
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ISVSuiteTicketAssemblerTest {

    @Test
    fun testToISVSuiteTicket(){
        val isvSuiteTicketDTO = ISVSuiteTicketDTO(
            suiteId = UUID.randomUUID().toString(),
            clientType = ISVClientType.WorkPlusISV.toString(),
            suiteTicket = UUID.randomUUID().toString()
        )
        val isvSuiteTicket = toISVSuiteTicket(isvSuiteTicketDTO)
        Assertions.assertNotNull(isvSuiteTicket)
    }

    @Test
    fun testToISVSuiteTicketDTO(){
        val isvSuiteTicket = randomSuiteTicket()
        val isvSuiteTicketDTO = toISVSuiteTicketDTO(isvSuiteTicket)
        Assertions.assertNotNull(isvSuiteTicketDTO)
    }

    private fun randomSuiteTicket(): ISVSuiteTicket {
        val suiteTicket = ISVSuiteTicket()
        suiteTicket.suiteId = UUID.randomUUID().toString()
        suiteTicket.clientType = ISVClientType.WorkPlusISV
        suiteTicket.suiteTicket = UUID.randomUUID().toString()
        return suiteTicket
    }
}