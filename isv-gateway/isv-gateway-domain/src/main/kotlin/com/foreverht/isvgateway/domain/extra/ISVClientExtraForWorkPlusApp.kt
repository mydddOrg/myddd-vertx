package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientExtraForWorkPlusApp : ISVClientExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusApp
    }

    lateinit var clientId:String

    lateinit var clientSecret:String

    lateinit var domainId:String

    lateinit var api:String

    lateinit var ownerId:String

    override fun primaryId(): String {
        return clientId
    }

}