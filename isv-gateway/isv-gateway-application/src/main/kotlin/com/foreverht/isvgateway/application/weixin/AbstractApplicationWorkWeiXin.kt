package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.domain.ISVClientToken
import io.vertx.core.Future

abstract class AbstractApplicationWorkWeiXin {

    suspend fun getRemoteAccessToken(accessToken:String): Future<ISVClientToken>{
        return try {
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}