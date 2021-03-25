package com.foreverht.isvgateway.api.dto

data class RequestTokenDTO(var clientId:String,val clientSecret:String,val domainId:String = "WorkWeiXin",var orgCode:String)
