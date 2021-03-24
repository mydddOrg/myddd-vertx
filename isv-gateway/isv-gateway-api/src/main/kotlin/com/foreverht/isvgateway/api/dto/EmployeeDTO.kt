package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject

data class EmployeeDTO(var userId:String,var name:String,var avatar:String? = null,var mobile:String? = null,var positions:List<PositionDTO> = emptyList()) {

    companion object {
        fun createInstanceFromJsomObject(jsonObject: JsonObject):EmployeeDTO{
            val userId = jsonObject.getString("user_id")
            val name = jsonObject.getString("nickname")
            val avatar = jsonObject.getString("avatar")
            val mobile = jsonObject.getString("avatar")

            val positionJsonArray = jsonObject.getJsonArray("positions")
            val positions = mutableListOf<PositionDTO>()
            positionJsonArray.forEach{
                positions.add(PositionDTO.createInstanceFromJsonObject(it as JsonObject))
            }

            return EmployeeDTO(userId = userId,name = name,avatar = avatar,mobile = mobile,positions = positions)
        }
    }
}
