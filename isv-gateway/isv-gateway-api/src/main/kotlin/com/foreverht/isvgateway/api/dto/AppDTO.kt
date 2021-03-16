package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject

data class AppDTO(var appId:String,var name:String,val icon:String? = null){

    companion object {
        fun createInstanceFromJson(jsonObject: JsonObject):AppDTO {
            val appId = jsonObject.getString("app_id")
            val name = jsonObject.getString("name")
            val icon = jsonObject.getString("icon")

            return AppDTO(appId = appId,name = name,icon = icon)
        }
    }

}