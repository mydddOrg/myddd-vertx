package com.foreverht.isvgateway.api.dto

data class ISVClientDTO(var clientId:String,var clientSecret:String? = null,var clientName:String,var callback:String,var description:String? = null,var extra:ISVClientExtraDTO)