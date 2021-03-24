package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.AppApplication
import com.foreverht.isvgateway.api.dto.AppDTO
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.resultSuccessForWorkWeiXin
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForWorkWeiXin
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class AppApplicationWorkWeiXin:AbstractApplication(),AppApplication {

    private val weiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }

    private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    override suspend fun getAdminList(isvAccessToken: String): Future<List<EmployeeDTO>> {
        return try {
            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val clientExtra = isvClientToken.client.extra as ISVClientExtraForWorkWeiXin
            val clientAuthExtra = isvClientToken.client.clientAuthExtra as ISVClientAuthExtraForWorkWeiXin

            val agentId = weiXinApplication.queryAgentId(isvClientToken.accessToken()).await()

            val isvAuthCode = ISVAuthCode.queryPermanentAuthCode(suiteId = clientExtra.suiteId,domainId = ISVAuthCode.WORK_WEI_XIN,orgCode = isvClientToken.orgCode,ISVClientType.WorkWeiXin).await()
            if(Objects.isNull(isvAuthCode)){
                throw BusinessLogicException(ISVErrorCode.PERMANENT_CODE_NOT_FOUND)
            }

            val requestBody = json {
                obj(
                    "auth_corpid" to isvAuthCode!!.orgCode,
                    "agentid" to agentId
                )
            }

            val response = webClient.postAbs("$WORK_WEI_XIN_SERVICE_API/get_admin_list?suite_access_token=${clientAuthExtra.suiteAccessToken}")
                .sendJsonObject(requestBody)
                .await()

            if(response.resultSuccessForWorkWeiXin()){
                val body = response.bodyAsJsonObject()
                val admins = body.getJsonArray("admin")
                val employeeList = mutableListOf<EmployeeDTO>()
                admins.forEach {
                    val adminJson = it as JsonObject
                    employeeList.add(EmployeeDTO(userId = adminJson.getString("userid"),name = adminJson.getString("open_userid")))
                }
                Future.succeededFuture(employeeList)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun getAppDetail(isvAccessToken: String): Future<AppDTO> {
        return try {
            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val clientExtra = isvClientToken.client.extra as ISVClientExtraForWorkWeiXin
            val clientAuthExtra = isvClientToken.client.clientAuthExtra as ISVClientAuthExtraForWorkWeiXin

            val isvAuthCode = ISVAuthCode.queryPermanentAuthCode(suiteId = clientExtra.suiteId,domainId = ISVAuthCode.WORK_WEI_XIN,orgCode = isvClientToken.orgCode,ISVClientType.WorkWeiXin).await()
            if(Objects.isNull(isvAuthCode)){
                throw BusinessLogicException(ISVErrorCode.PERMANENT_CODE_NOT_FOUND)
            }

            val requestBody = json {
                obj(
                    "auth_corpid" to isvAuthCode!!.orgCode,
                    "permanent_code" to isvAuthCode.permanentAuthCode
                )
            }

            val response = webClient.postAbs("$WORK_WEI_XIN_SERVICE_API/get_auth_info?suite_access_token=${clientAuthExtra.suiteAccessToken}")
                .sendJsonObject(requestBody)
                .await()

            if(response.resultSuccessForWorkWeiXin()){
                val bodyJson = response.bodyAsJsonObject()
                val agent = bodyJson.getJsonObject("auth_info").getJsonArray("agent").getJsonObject(0)
                val appDTO = AppDTO(
                    appId = agent.getString("agentid"),
                    name = agent.getString("name"),
                    icon = agent.getString("square_logo_url")
                )
                Future.succeededFuture(appDTO)
            }else{
                Future.failedFuture(response.bodyAsString())
            }


        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}