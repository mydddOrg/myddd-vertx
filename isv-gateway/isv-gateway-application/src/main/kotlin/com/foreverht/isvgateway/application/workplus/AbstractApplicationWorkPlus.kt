package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.domain.ISVClientToken
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import io.vertx.core.Future
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import java.util.*

abstract class AbstractApplicationWorkPlus {

    val logger: Logger by lazy { LoggerFactory.getLogger(AbstractApplicationWorkPlus::class.java) }

    suspend fun getRemoteAccessToken(accessToken:String): Future<ISVClientToken> {
        return try {
            val isvClientToken = ISVClientToken.queryByToken(token = accessToken).await()
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