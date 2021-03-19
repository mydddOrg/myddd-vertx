package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject

class ISVClientTokenExtraForWorkPlusApp : ISVClientTokenExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusApp

    }

    companion object {
        val logger by lazy { LoggerFactory.getLogger(ISVClientTokenExtraForWorkPlusApp::class.java) }


        fun createInstanceFormJsonObject(result:JsonObject):ISVClientTokenExtraForWorkPlusApp{
            val extra = ISVClientTokenExtraForWorkPlusApp()
            extra.clientId = result.getString("client_id")
            extra.accessToken = result.getString("access_token")
            extra.refreshToken = result.getString("refresh_token")
            extra.expireTime = result.getLong("expire_time")
            extra.issuedTime = result.getLong("issued_time")
            return extra
        }
    }

    lateinit var accessToken:String

    lateinit var refreshToken:String

    lateinit var clientId:String

    var expireTime:Long = 0

    var issuedTime:Long = 0

    override fun accessTokenValid(): Boolean {
        if(System.currentTimeMillis() < expireTime){
            logger.info("【INVALID】：$expireTime - ${System.currentTimeMillis()}")
        }
        return expireTime > System.currentTimeMillis()
    }

    override fun accessToken(): String {
        return accessToken
    }



}