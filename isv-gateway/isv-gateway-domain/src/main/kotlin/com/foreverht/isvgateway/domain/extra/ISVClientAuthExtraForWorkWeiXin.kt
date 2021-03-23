package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientAuthExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientAuthExtraForWorkWeiXin: ISVClientAuthExtra() {

    init {
        this.clientType = ISVClientType.WorkWeiXin
    }

    companion object{

        fun createInstanceFromJson(suiteAccessToken:String,expiresIn:Long):ISVClientAuthExtra {
            val extra = ISVClientAuthExtraForWorkWeiXin()
            extra.suiteAccessToken = suiteAccessToken
            extra.expireTime = System.currentTimeMillis() + expiresIn
            return extra
        }

    }

    lateinit var suiteAccessToken:String

    var expireTime:Long = 0

    override fun clientTokenValid(): Boolean {
        return System.currentTimeMillis() < expireTime
    }


}