package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientTokenExtraForWorkPlusISV : ISVClientTokenExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusISV
    }

    companion object {

        fun createInstance(accessToken:String,expireTime:Long):ISVClientTokenExtraForWorkPlusISV {
            val isvClientTokenExtra = ISVClientTokenExtraForWorkPlusISV()
            isvClientTokenExtra.accessToken = accessToken
            isvClientTokenExtra.expireTime = expireTime
            return isvClientTokenExtra
        }
    }

    lateinit var accessToken:String

    var expireTime:Long = 0

    override fun accessTokenValid(): Boolean {
        return expireTime > System.currentTimeMillis()
    }

    override fun accessToken(): String {
        return accessToken
    }
}