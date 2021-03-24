package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject

data class PositionDTO(var orgCode:String,var orgId:String,var jobTitle:String,var path:String,var primary:Boolean = true){
    companion object {
        fun createInstanceFromJsonObject(jsonObject: JsonObject):PositionDTO{
            val orgId = jsonObject.getString("org_id")
            val orgCode = jsonObject.getString("code")
            val path = jsonObject.getString("path")
            val jobTitle = jsonObject.getString("job_title")
            val primary = jsonObject.getBoolean("primary")
            return PositionDTO(orgCode = orgCode,orgId = orgId,path = path,jobTitle = jobTitle,primary = primary)
        }
    }
}