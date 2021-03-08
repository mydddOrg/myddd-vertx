package com.foreverht.isvgateway.api.dto


data class ISVClientDTO  constructor(
    var clientId:String? = null,
    var clientSecret:String? = null,
    var clientName:String,
    var callback:String,
    var description:String? = null,
    var extra:ISVClientExtraDTO
    )