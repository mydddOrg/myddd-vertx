package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientExtra
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
    val extra = toISVClientExtra(isvClientDTO.extra)
    checkNotNull(extra)
    return ISVClient.createClient(clientId = isvClientDTO.clientId,clientName = isvClientDTO.clientName,callback = isvClientDTO.callback,extra = extra,description = isvClientDTO.description)
}

fun toISVClientExtra(isvExtraDTO: ISVClientExtraDTO):ISVClientExtra? {
    return when (isvExtraDTO){
        is ISVClientExtraForWorkPlusDTO -> {
            val isvClientExtra = ISVClientExtraForWorkPlusApp()
            isvClientExtra.clientId = isvExtraDTO.clientId
            isvClientExtra.clientSecret = isvExtraDTO.clientSecret
            isvClientExtra.domainId = isvExtraDTO.domainId
            isvClientExtra.api = isvExtraDTO.api
            isvClientExtra.ownerId = isvExtraDTO.ownerId
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
                domainId = isvClientExtra.domainId,
                ownerId = isvClientExtra.ownerId
            )
        }
        else -> null
    }
}