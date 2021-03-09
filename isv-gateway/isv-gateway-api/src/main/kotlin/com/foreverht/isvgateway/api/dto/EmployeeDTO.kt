package com.foreverht.isvgateway.api.dto

data class EmployeeDTO(var userId:String,var name:String,var avatar:String?,var mobile:String?,var positions:List<PositionDTO> = emptyList())
