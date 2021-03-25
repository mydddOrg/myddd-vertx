package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.domain.ISVAuthCode
import io.vertx.core.Future

class WeiXinSyncDataApplication {

    companion object {
        private const val WORK_WEI_XIN_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/department"
    }

    suspend fun syncOrganization(isvAuthCode: ISVAuthCode): Future<Unit> {
        TODO("Not yet implemented")
    }
}