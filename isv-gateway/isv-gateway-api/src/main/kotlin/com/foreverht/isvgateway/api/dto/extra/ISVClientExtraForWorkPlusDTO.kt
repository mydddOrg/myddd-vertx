package com.foreverht.isvgateway.api.dto.extra

import com.fasterxml.jackson.annotation.JsonTypeName
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO

@JsonTypeName("WorkPlusApp")
data class ISVClientExtraForWorkPlusDTO constructor(
    var clientId:String,
    var clientSecret:String,
    var domainId:String,
    var api:String,
    var ownerId:String
    ) : ISVClientExtraDTO(clientType = "WorkPlusApp")
