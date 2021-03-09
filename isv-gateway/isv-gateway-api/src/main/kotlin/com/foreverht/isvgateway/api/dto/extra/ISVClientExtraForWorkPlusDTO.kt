package com.foreverht.isvgateway.api.dto.extra

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO

@JsonTypeName("WorkPlus_App")
data class ISVClientExtraForWorkPlusDTO constructor(
    var clientId:String,
    var clientSecret:String,
    var domainId:String,
    var api:String,
    var ownerId:String
    ) : ISVClientExtraDTO(clientType = "WorkPlus_App")
