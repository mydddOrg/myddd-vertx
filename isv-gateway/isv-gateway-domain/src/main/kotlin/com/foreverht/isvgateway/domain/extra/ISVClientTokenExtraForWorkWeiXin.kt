package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientTokenExtraForWorkWeiXin: ISVClientTokenExtra() {

    init {
        this.clientType = ISVClientType.WorkWeiXin
    }

    companion object {

        fun createInstance(corpAccessToken:String,expiresIn:Long):ISVClientTokenExtraForWorkWeiXin{
            val extra = ISVClientTokenExtraForWorkWeiXin()
            extra.corpAccessToken = corpAccessToken
            extra.expireTime = System.currentTimeMillis() + expiresIn
            return extra
        }
    }

    lateinit var corpAccessToken:String

    var expireTime:Long = 0

    override fun accessTokenValid(): Boolean {
        return System.currentTimeMillis() < expireTime
    }

    override fun accessToken(): String {
        return corpAccessToken
    }
}