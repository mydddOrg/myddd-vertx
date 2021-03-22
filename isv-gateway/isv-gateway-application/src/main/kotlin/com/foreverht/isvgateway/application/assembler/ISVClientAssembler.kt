package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusISVDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkWeiXinDTO
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin


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
            isvClientExtra.appKey = isvExtraDTO.appKey
            isvClientExtra.appSecret = isvExtraDTO.appSecret
            isvClientExtra.domainId = isvExtraDTO.domainId
            isvClientExtra.api = isvExtraDTO.api
            isvClientExtra.ownerId = isvExtraDTO.ownerId
            isvClientExtra
        }
        is ISVClientExtraForWorkPlusISVDTO -> {
            val isvClientExtra = ISVClientExtraForWorkPlusISV()
            isvClientExtra.suiteKey = isvExtraDTO.suiteKey
            isvClientExtra.suiteSecret = isvExtraDTO.suiteSecret
            isvClientExtra.vendorKey = isvExtraDTO.vendorKey
            isvClientExtra.token = isvExtraDTO.token
            isvClientExtra.encryptSecret = isvExtraDTO.encryptSecret
            isvClientExtra.isvApi = isvExtraDTO.isvApi
            isvClientExtra.appId = isvExtraDTO.appId
            isvClientExtra
        }
        is ISVClientExtraForWorkWeiXinDTO -> {
            val isvClientExtra = ISVClientExtraForWorkWeiXin()
            isvClientExtra.corpId = isvExtraDTO.corpId
            isvClientExtra.providerSecret = isvExtraDTO.providerSecret
            isvClientExtra.suiteId = isvExtraDTO.suiteId
            isvClientExtra.suiteSecret = isvExtraDTO.suiteSecret
            isvClientExtra.token = isvExtraDTO.token
            isvClientExtra.encodingAESKey = isvExtraDTO.encodingAESKey
            isvClientExtra
        }
        else -> null
    }
}

fun toISVClientExtraDTO(isvClientExtra: ISVClientExtra) : ISVClientExtraDTO? {
    return when(isvClientExtra){
        is ISVClientExtraForWorkPlusApp -> {
            ISVClientExtraForWorkPlusDTO(
                appKey = isvClientExtra.appKey,
                appSecret = isvClientExtra.appSecret,
                api = isvClientExtra.api,
                domainId = isvClientExtra.domainId,
                ownerId = isvClientExtra.ownerId
            )
        }
        is ISVClientExtraForWorkPlusISV -> {
            ISVClientExtraForWorkPlusISVDTO(
                suiteKey = isvClientExtra.suiteKey,
                suiteSecret = isvClientExtra.suiteSecret,
                vendorKey = isvClientExtra.vendorKey,
                token = isvClientExtra.token,
                encryptSecret = isvClientExtra.encryptSecret,
                isvApi = isvClientExtra.isvApi,
                appId = isvClientExtra.appId
            )
        }
        is ISVClientExtraForWorkWeiXin -> {
            ISVClientExtraForWorkWeiXinDTO(
                corpId = isvClientExtra.corpId,
                providerSecret = isvClientExtra.providerSecret,
                suiteId = isvClientExtra.suiteId,
                suiteSecret =  isvClientExtra.suiteSecret,
                token = isvClientExtra.token,
                encodingAESKey = isvClientExtra.encodingAESKey
            )
        }
        else -> null
    }
}