package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteTicket

fun toISVSuiteTicket(isvSuiteTicketDTO: ISVSuiteTicketDTO):ISVSuiteTicket {
    val suiteTicket = ISVSuiteTicket()
    suiteTicket.suiteId = isvSuiteTicketDTO.suiteId
    suiteTicket.suiteTicket = isvSuiteTicketDTO.suiteTicket
    suiteTicket.clientType = ISVClientType.valueOf(isvSuiteTicketDTO.clientType)
    return suiteTicket
}

fun toISVSuiteTicketDTO(isvSuiteTicket: ISVSuiteTicket):ISVSuiteTicketDTO {
    return ISVSuiteTicketDTO(
        suiteId = isvSuiteTicket.suiteId,
        suiteTicket = isvSuiteTicket.suiteTicket,
        clientType = isvSuiteTicket.clientType.toString()
    )
}

