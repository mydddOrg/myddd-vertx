package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject

data class OrganizationDTO(var orgId:String,var orgCode:String,var domainId:String,var path:String,var name:String,var logo:String? = null) {


    companion object {
        fun createInstanceFromJsonObject(jsonObject: JsonObject):OrganizationDTO {
            val orgId = jsonObject.getString("id")
            val orgCode = jsonObject.getString("org_code")
            val path = jsonObject.getString("path")
            val logo = jsonObject.getString("logo")
            val name = jsonObject.getString("name")
            val domainId = jsonObject.getString("domain_id")

            return OrganizationDTO(orgId = orgId, orgCode = orgCode, path = path, logo = logo,name = name,domainId = domainId)
        }
    }

}