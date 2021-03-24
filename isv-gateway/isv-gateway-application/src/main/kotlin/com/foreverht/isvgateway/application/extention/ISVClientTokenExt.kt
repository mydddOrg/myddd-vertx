package com.foreverht.isvgateway.application.extention

import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusISV
import org.myddd.vertx.base.BusinessLogicException

fun String.deleteLastSlash():String {
    return if(this.endsWith("/")) this.substring(0,this.length - 1) else this
}

private const val WORK_WEI_XIN_API = "https://qyapi.weixin.qq.com/cgi-bin/service"

fun ISVClientToken.api():String{
    return when(this.client.clientType){
        ISVClientType.WorkPlusApp -> {
            val extra = this.client.extra as ISVClientExtraForWorkPlusApp
            extra.api.deleteLastSlash()
        }
        ISVClientType.WorkPlusISV -> {
            val extra = this.extra as ISVClientTokenExtraForWorkPlusISV
            extra.accessEndpoint.deleteLastSlash()
        }
        ISVClientType.WorkWeiXin -> {
            WORK_WEI_XIN_API
        }
        else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
    }
}

fun ISVClientToken.accessToken():String {
    return this.extra.accessToken()
}

fun ISVClientToken.appId():String {
    return when(this.client.clientType){
        ISVClientType.WorkPlusApp -> {
            val extra = this.client.extra as ISVClientExtraForWorkPlusApp
            extra.appKey
        }
        ISVClientType.WorkPlusISV -> {
            val extra = this.client.extra as ISVClientExtraForWorkPlusISV
            extra.appId
        }
        else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
    }
}

fun ISVClientToken.appType():String {
    return when(this.client.clientType){
        ISVClientType.WorkPlusApp -> "NATIVE"
        ISVClientType.WorkPlusISV -> "ISV"
        else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
    }
}