package com.foreverht.isvgateway.api.dto

data class OrgPageQueryDTO(val accessToken:String, val orgCode:String, val orgId:String? = null, val limit:Int = 50, val skip:Int = 0)