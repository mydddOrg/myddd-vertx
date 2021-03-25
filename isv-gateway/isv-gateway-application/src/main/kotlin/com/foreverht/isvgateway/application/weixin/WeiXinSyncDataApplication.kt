package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.application.extention.*
import com.foreverht.isvgateway.domain.*
import io.vertx.core.Future
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class WeiXinSyncDataApplication {

    private val weiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }
    private val webClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }
    private val logger by lazy { LoggerFactory.getLogger(WeiXinSyncDataApplication::class.java) }

    companion object {
        private const val WORK_WEI_XIN_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/department"
        private const val WORK_WEI_XIN_USER = "https://qyapi.weixin.qq.com/cgi-bin/user"

    }

    suspend fun syncAllData(clientId:String, isvAuthCode: ISVAuthCode): Future<Unit> {
        return try {
            val isvClientToken = requestCorpAccessToken(clientId = clientId,corpId = isvAuthCode.orgCode).await()
            syncOrganizationData(corpAccessToken = isvClientToken.accessToken(),isvAuthCode = isvAuthCode).await()
            syncEmployeeData(corpAccessToken = isvClientToken.accessToken(),isvAuthCode = isvAuthCode).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun requestCorpAccessToken(clientId: String,corpId:String):Future<ISVClientToken>{
        return try {
            var isvClientToken = ISVClientToken.queryClientToken(clientId = clientId,domainId = ISVAuthCode.WORK_WEI_XIN,orgCode = corpId).await()
            if(Objects.isNull(isvClientToken)){
                isvClientToken = weiXinApplication.requestCorpAccessToken(clientId = clientId,corpId = corpId).await()
            }

            if(Objects.nonNull(isvClientToken)){

                Future.succeededFuture(isvClientToken)
            }else{
                throw BusinessLogicException(ISVErrorCode.REMOTE_CLIENT_TOKEN_REQUEST_FAIL)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


    internal suspend fun syncOrganizationData(corpAccessToken: String,isvAuthCode: ISVAuthCode):Future<List<ProxyOrganization>>{
        return try {
            val response = webClient.getAbs("$WORK_WEI_XIN_DEPARTMENT/list?access_token=$corpAccessToken&id=ID")
                .send()
                .await()
            if(response.resultSuccessForWorkWeiXin()){
                val body = response.bodyAsJsonObject()
                val departmentList = body.getJsonArray("department")
                val organizationList = mutableListOf<ProxyOrganization>()
                val organizationMap = mutableMapOf<String,ProxyOrganization>()
                departmentList.forEach { department ->
                    val organization = (department as JsonObject).toProxyOrganization(authCode = isvAuthCode)
                    organizationList.add(organization)
                    organizationMap[organization.orgId] = organization
                }
                organizationList.forEach {
                    it.parseAbsolutePath(organizationMap)
                }
                ProxyOrganization.batchSaveOrganization(isvAuthCodeId = isvAuthCode.id,orgList = organizationList).await()
                Future.succeededFuture(organizationList)
            }else{
                Future.failedFuture(response.bodyAsString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    internal suspend fun syncEmployeeData(corpAccessToken: String,isvAuthCode: ISVAuthCode):Future<List<ProxyEmployee>>{
        return try {
            val response = webClient.getAbs("$WORK_WEI_XIN_USER/list?access_token=$corpAccessToken&department_id=1&fetch_child=1")
                .send()
                .await()
            if(response.resultSuccessForWorkWeiXin()){
                val body = response.bodyAsJsonObject()
                val userList = body.getJsonArray("userlist")
                val employeeList = mutableListOf<ProxyEmployee>()
                val organizationList = ProxyOrganization.queryOrganizations(authCodeId = isvAuthCode.id).await()

                val organizationMap = mutableMapOf<String,ProxyOrganization>()
                organizationList.forEach {
                    organizationMap[it.orgId] = it
                }

                userList.forEach {
                    val proxyEmployee = (it as JsonObject).toProxyEmployee(authCode = isvAuthCode,organizationMap = organizationMap)
                    employeeList.add(proxyEmployee)
                }

                ProxyEmployee.batchSaveEmployeeList(isvAuthCodeId = isvAuthCode.id,employeeList = employeeList).await()
                Future.succeededFuture()
            }else{
                logger.debug(response.bodyAsString())
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}