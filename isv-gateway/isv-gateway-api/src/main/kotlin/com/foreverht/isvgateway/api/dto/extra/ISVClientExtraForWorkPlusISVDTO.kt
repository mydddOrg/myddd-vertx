package com.foreverht.isvgateway.api.dto.extra

import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO

data class ISVClientExtraForWorkPlusISVDTO(
    var suiteKey:String,
    var suiteSecret:String,
    var vendorKey:String,
    var token:String,
    var encryptSecret:String,
    var isvApi:String,
    var appId:String,
): ISVClientExtraDTO(clientType = "WorkPlusISV")