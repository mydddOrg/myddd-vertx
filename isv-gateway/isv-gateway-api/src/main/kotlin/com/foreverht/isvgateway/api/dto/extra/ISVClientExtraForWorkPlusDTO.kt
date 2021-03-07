package com.foreverht.isvgateway.api.dto.extra

import com.fasterxml.jackson.annotation.JsonTypeName
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO

@JsonTypeName("WorkPlus")
data class ISVClientExtraForWorkPlusDTO(var clientId:String, var clientSecret:String, var domainId:String, var api:String) : ISVClientExtraDTO(clientType = "WorkPlus")
