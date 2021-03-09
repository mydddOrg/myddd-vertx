package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AccessTokenApplication
import io.vertx.core.Future

class WorkPlusAccessTokenApplication : AccessTokenApplication{

    override fun requestRequestAccessToken(clientId: String): Future<String> {
        TODO("Not yet implemented")
    }
}