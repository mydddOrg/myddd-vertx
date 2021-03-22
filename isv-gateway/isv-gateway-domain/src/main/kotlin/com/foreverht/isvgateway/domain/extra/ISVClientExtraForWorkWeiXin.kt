package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientExtraForWorkWeiXin : ISVClientExtra() {

    init {
        this.clientType = ISVClientType.WorkWeiXin
    }

    lateinit var corpId:String

    lateinit var providerSecret:String

    lateinit var suiteId:String

    lateinit var suiteSecret:String

    override fun primaryId(): String {
        return suiteId
    }

}