package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientExtraForWorkPlusISV : ISVClientExtra() {

    init {
        this.clientType = ISVClientType.WorkPlusISV
    }

    lateinit var suiteKey:String

    lateinit var suiteSecret:String

    lateinit var vendorKey:String

    lateinit var token:String

    lateinit var encryptSecret:String

    lateinit var isvApi:String

    lateinit var appId:String

    override fun primaryId(): String {
        return suiteKey
    }

}