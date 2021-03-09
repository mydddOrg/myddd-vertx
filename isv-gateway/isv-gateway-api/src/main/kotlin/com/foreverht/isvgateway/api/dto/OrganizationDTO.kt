package com.foreverht.isvgateway.api.dto

data class OrganizationDTO(var orgId:String,var path:String,var name:String,var logo:String?,var positions:List<PositionDTO>)