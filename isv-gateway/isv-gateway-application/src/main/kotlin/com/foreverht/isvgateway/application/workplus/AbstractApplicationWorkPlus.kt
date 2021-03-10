package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.domain.ISVErrorCode
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

abstract class AbstractApplicationWorkPlus {

    private val accessTokenApplication: AccessTokenApplication by lazy { InstanceFactory.getInstance(
        AccessTokenApplication::class.java,"WorkPlusApp") }
    private val isvClientApplication: ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
    private val logger: Logger by lazy { LoggerFactory.getLogger(AbstractApplicationWorkPlus::class.java) }

    suspend fun getRemoteAccessToken(clientId: String): Pair<ISVClientExtraForWorkPlusDTO, String?> {
        val isvClient = isvClientApplication.queryClientByClientId(clientId).await()
        if (Objects.isNull(isvClient)) throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
        val extra = isvClient!!.extra as ISVClientExtraForWorkPlusDTO
        val accessToken = accessTokenApplication.requestRequestAccessToken(clientId = clientId).await()
        return Pair(extra, accessToken)
    }
}