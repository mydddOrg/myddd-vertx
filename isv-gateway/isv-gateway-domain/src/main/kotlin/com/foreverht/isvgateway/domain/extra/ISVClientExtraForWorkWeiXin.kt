package com.foreverht.isvgateway.domain.extra

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType

class ISVClientExtraForWorkWeiXin : ISVClientExtra() {

    init {
        this.clientType = ISVClientType.WorkWeiXin
    }

    lateinit var corpid:String

    lateinit var suiteId:String

    lateinit var suiteSecret:String
}