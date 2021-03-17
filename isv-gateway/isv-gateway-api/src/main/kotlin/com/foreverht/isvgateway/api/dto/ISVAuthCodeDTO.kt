package com.foreverht.isvgateway.api.dto

data class ISVAuthCodeDTO(
    val suiteId:String,
    val clientType:String,
    val authStatus:String,
    val domainId:String? = null,
    var orgId:String,
    var temporaryAuthCode:String,
    var permanentAuthCode:String? = null
)
