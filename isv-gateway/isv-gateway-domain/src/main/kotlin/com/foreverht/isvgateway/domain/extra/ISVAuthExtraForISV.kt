package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVAuthExtra
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.json.JsonObject

class ISVAuthExtraForISV : ISVAuthExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusISV
    }

    companion object {
        fun createInstanceFromJson(jsonObject: JsonObject):ISVAuthExtraForISV{
            val extra = ISVAuthExtraForISV()
            extra.apiAccessToken = jsonObject.getString("api_access_token")
            extra.websiteEndpoint = jsonObject.getString("website_endpoint")
            extra.accessEndpoint = jsonObject.getString("access_endpoint")
            extra.expireTime = jsonObject.getLong("expire_time")
            return extra
        }
    }

    lateinit var apiAccessToken:String

    lateinit var accessEndpoint:String

    lateinit var websiteEndpoint:String

    var expireTime:Long = 0



}