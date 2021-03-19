package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientAuthExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientAuthExtraForISV : ISVClientAuthExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusISV
    }

    companion object {

        fun createInstance(accessToken:String,expireTime:Long):ISVClientAuthExtraForISV {
            val isvClientTokenExtra = ISVClientAuthExtraForISV()
            isvClientTokenExtra.accessToken = accessToken
            isvClientTokenExtra.expireTime = expireTime
            return isvClientTokenExtra
        }
    }

    lateinit var accessToken:String

    var expireTime:Long = 0

    override fun clientTokenValid(): Boolean {
        return System.currentTimeMillis() < expireTime
    }

}