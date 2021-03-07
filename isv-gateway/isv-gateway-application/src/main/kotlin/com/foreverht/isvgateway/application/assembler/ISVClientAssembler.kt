package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp


fun toISVClientDTO(isvClient:ISVClient) : ISVClientDTO {
    return ISVClientDTO(
        clientName = isvClient.clientName,
        callback = isvClient.callback,
        description = isvClient.description,
        extra = toISVClientExtraDTO(isvClient.extra)!!,
        clientId = isvClient.clientId,
        clientSecret = isvClient.oauth2Client.clientSecret
    )
}

fun toISVClient(isvClientDTO: ISVClientDTO) : ISVClient {
    val isvClient = ISVClient()
    isvClient.clientName = isvClientDTO.clientName
    isvClient.callback = isvClientDTO.callback
    isvClient.description = isvClientDTO.description
    toISVClientExtra(isvClientDTO.extra)?.also { isvClient.extra = it}
    isvClient.clientType = toISVClientType(isvClientDTO.extra.clientType)
    return isvClient
}

private fun toISVClientType(clientType:String) : ISVClientType {
    return when(clientType) {
        else -> ISVClientType.WorkPlus
    }
}

fun toISVClientExtra(isvExtraDTO: ISVClientExtraDTO):ISVClientExtra? {
    return when (isvExtraDTO){
        is ISVClientExtraForWorkPlusDTO -> {
            val isvClientExtra = ISVClientExtraForWorkPlusApp()
            isvClientExtra.clientId = isvExtraDTO.clientId
            isvClientExtra.clientSecret = isvExtraDTO.clientSecret
            isvClientExtra.domainId = isvExtraDTO.domainId
            isvClientExtra.api = isvExtraDTO.api

            isvClientExtra
        }
        else -> null
    }
}

fun toISVClientExtraDTO(isvClientExtra: ISVClientExtra) : ISVClientExtraDTO? {
    return when(isvClientExtra){
        is ISVClientExtraForWorkPlusApp -> {
            ISVClientExtraForWorkPlusDTO(
                clientId = isvClientExtra.clientId,
                clientSecret = isvClientExtra.clientSecret,
                api = isvClientExtra.api,
                domainId = isvClientExtra.domainId
            )
        }
        else -> null
    }
}