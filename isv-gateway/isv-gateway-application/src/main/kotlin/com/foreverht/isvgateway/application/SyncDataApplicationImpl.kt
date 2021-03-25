package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.SyncDataApplication
import com.foreverht.isvgateway.application.weixin.WeiXinSyncDataApplication
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory

class SyncDataApplicationImpl:SyncDataApplication {

    private val weiXinSyncDataApplication by lazy { InstanceFactory.getInstance(WeiXinSyncDataApplication::class.java) }

    override suspend fun syncOrganization(clientId: String, domainId:String, orgCode: String): Future<Unit> {
        return try {
            val isvClient = ISVClient.queryClient(clientId).await()
            requireNotNull(isvClient){
                "CLIENT_ID_NOT_FOUND"
            }
            return when(isvClient.clientType){
                ISVClientType.WorkWeiXin -> {
                    val extra = isvClient.extra as ISVClientExtraForWorkWeiXin
                    val isvAuthCode = ISVAuthCode.queryPermanentAuthCode(suiteId = extra.suiteId,domainId = domainId,orgCode = orgCode,clientType = ISVClientType.WorkWeiXin).await()
                    requireNotNull(isvAuthCode){
                        "AUTH_CODE_NOT_FOUND"
                    }
                    weiXinSyncDataApplication.syncAllData(clientId,isvAuthCode)
                }
                else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
            }


        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}