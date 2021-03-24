package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVErrorCode
import io.vertx.core.Future
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import java.util.*

abstract class AbstractApplication {

    val logger: Logger by lazy { LoggerFactory.getLogger(AbstractApplication::class.java) }

    companion object {
        //微信服务API
        const val WORK_WEI_XIN_SERVICE_API = "https://qyapi.weixin.qq.com/cgi-bin/service"
        //微信调用第三方企业api
        const val WORK_WEI_XIN_AGENT_API = "https://qyapi.weixin.qq.com/cgi-bin/agent"
    }

    suspend fun getRemoteAccessToken(isvAccessToken:String): Future<ISVClientToken> {
        return try {
            val isvClientToken = ISVClientToken.queryByToken(token = isvAccessToken).await()
            if(Objects.nonNull(isvClientToken) && isvClientToken!!.extra.accessTokenValid()){
                Future.succeededFuture(isvClientToken)
            }else{
                throw BusinessLogicException(ISVErrorCode.ACCESS_TOKEN_INVALID)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}