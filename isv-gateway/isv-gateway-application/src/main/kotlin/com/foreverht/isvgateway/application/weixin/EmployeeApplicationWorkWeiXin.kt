package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.EmployeeApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.assembler.toEmployeeDTO
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.ProxyEmployee
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam
import java.util.*

class EmployeeApplicationWorkWeiXin:AbstractApplication(),EmployeeApplication {

    private val queryChannel by lazy { InstanceFactory.getInstance(QueryChannel::class.java) }

    override suspend fun queryEmployeeById(isvAccessToken: String, orgCode: String, userId: String): Future<EmployeeDTO> {
        return try {
            val isvAuthCode = getAuthCode(isvAccessToken = isvAccessToken,orgCode = orgCode).await()

            val queryEmployee = ProxyEmployee.queryEmployee(authCodeId = isvAuthCode.id,userId = userId).await()
            if(Objects.isNull(queryEmployee)) throw BusinessLogicException(ISVErrorCode.USER_ID_NOT_FOUND)

            Future.succeededFuture(toEmployeeDTO(queryEmployee!!))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun batchQueryEmployeeByIds(isvAccessToken: String, orgCode: String, userIdList: List<String>): Future<List<EmployeeDTO>> {
        return try {
            val isvAuthCode = getAuthCode(isvAccessToken = isvAccessToken,orgCode = orgCode).await()

            val list = queryChannel.queryList(QueryParam(
                clazz = ProxyEmployee::class.java,
                sql = "from ProxyEmployee where userId in (:userIds) and authCode.id = :authCodeId",
                params = mapOf(
                    "userIds" to userIdList,
                    "authCodeId" to isvAuthCode.id
                )
            )).await()
            Future.succeededFuture(list.map { toEmployeeDTO(it) })
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun searchEmployees(isvAccessToken: String, orgCode: String, query: String): Future<List<EmployeeDTO>> {
        return try {
            val isvAuthCode = getAuthCode(isvAccessToken = isvAccessToken,orgCode = orgCode).await()

            val list = queryChannel.queryList(QueryParam(
                clazz = ProxyEmployee::class.java,
                sql = "from ProxyEmployee where userId like :search and authCode.id = :authCodeId",
                params = mapOf(
                    "search" to "%${query}%",
                    "authCodeId" to isvAuthCode.id
                )
            )).await()
            Future.succeededFuture(list.map { toEmployeeDTO(it) })
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}