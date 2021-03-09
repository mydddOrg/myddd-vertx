package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientTokenExtraForWorkPlusApp : ISVClientTokenExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusApp
    }

    lateinit var accessToken:String

    lateinit var refreshToken:String

    lateinit var clientId:String

    var expireTime:Long = 0

    var issuedTime:Long = 0

    override fun accessTokenValid(): Boolean {
        return expireTime > System.currentTimeMillis()
    }
}