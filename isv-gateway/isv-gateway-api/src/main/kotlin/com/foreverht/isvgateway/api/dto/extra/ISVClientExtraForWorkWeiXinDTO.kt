package com.foreverht.isvgateway.api.dto.extra

import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO

data class ISVClientExtraForWorkWeiXinDTO(
    val corpId:String,
    val providerSecret:String,
    val suiteId:String,
    val suiteSecret:String,
    val token:String,
    val encodingAESKey:String
): ISVClientExtraDTO(clientType = "WorkWeiXin")
