package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientExtraForWorkPlusApp : ISVClientExtra() {

    init {
        this.clientType = ISVClientType.WorkPlus
    }

    lateinit var clientId:String

    lateinit var clientSecret:String

    lateinit var domainId:String

    lateinit var api:String
}