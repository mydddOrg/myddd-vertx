package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.json.JsonObject

class ISVClientTokenExtraForWorkPlusISV : ISVClientTokenExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusISV
    }

    companion object {
        fun createInstanceFromJson(jsonObject: JsonObject):ISVClientTokenExtra{
            val extra = ISVClientTokenExtraForWorkPlusISV()
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

    override fun accessTokenValid(): Boolean {
        return System.currentTimeMillis() < expireTime
    }

    override fun accessToken(): String {
        return apiAccessToken
    }

}