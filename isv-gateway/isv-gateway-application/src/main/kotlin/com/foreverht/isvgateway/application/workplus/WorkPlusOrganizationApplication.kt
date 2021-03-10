package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.domain.ISVErrorCode
import io.vertx.core.Future
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class WorkPlusOrganizationApplication : OrganizationApplication {
    private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }
    private val accessTokenApplication:AccessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlusApp") }
    private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
    private val logger:Logger by lazy { LoggerFactory.getLogger(WorkPlusOrganizationApplication::class.java) }

    override suspend fun queryOrganizationById(
        clientId:String,
        orgCode: String,
        orgId: String?
    ): Future<OrganizationDTO> {
        return try {
            val (extra, accessToken) = getRemoteAccessToken(clientId)
            val requestUrl = "${extra.api}/admin/organizations/$orgCode/view?employee_limit=0&org_limit=0&org_id=$orgId&access_token=$accessToken"
            logger.debug("【Request URL】:$requestUrl" )
            val response = webClient.getAbs(requestUrl).send().await()

            val responseBody = response.bodyAsJsonObject()
            if(response.resultSuccess()){
                logger.debug(responseBody.toString())
                val resultList = responseBody.getJsonArray("result")
                if(resultList.list.size > 0){
                    val resultJsonObject:JsonObject = resultList.getJsonObject(0)
                    Future.succeededFuture(OrganizationDTO.createInstanceFromJsonObject(resultJsonObject))
                }else{
                    Future.failedFuture(responseBody.toString())
                }

            }else{
                logger.error(responseBody.toString())
                Future.failedFuture(responseBody.toString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryChildrenOrganizations(
        clientId:String,
        orgCode: String,
        orgId: String?,
        limit: Int,
        skip: Int
    ): Future<List<OrganizationDTO>> {
        return try {
            val (extra, accessToken) = getRemoteAccessToken(clientId)

            val requestUrl = "${extra.api}/admin/organizations/$orgCode/view?employee_limit=0&org_limit=$limit&org_skip=$skip&org_id=$orgId&access_token=$accessToken"
            logger.debug("【Request URL】:$requestUrl" )

            val response = webClient.getAbs(requestUrl).send().await()

            val responseBody = response.bodyAsJsonObject()
            if(response.resultSuccess()){
                logger.debug(responseBody.toString())
                val resultList = responseBody.getJsonArray("result")
                if(resultList.list.size > 0){
                    val resultJsonObject:JsonObject = resultList.getJsonObject(0)
                    val childrenList = resultJsonObject.getJsonArray("children")
                    val children = mutableListOf<OrganizationDTO>()

                    childrenList.forEach{
                        children.add(OrganizationDTO.createInstanceFromJsonObject(it as JsonObject))
                    }
                    Future.succeededFuture(children)
                }else{
                    Future.failedFuture(responseBody.toString())
                }
            }else{
                logger.error(responseBody.toString())
                Future.failedFuture(responseBody.toString())
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryOrganizationEmployees(
        clientId:String,
        orgCode: String,
        orgId: String?,
        limit: Int,
        skip: Int
    ): Future<List<EmployeeDTO>> {
        return try {
            val (extra, accessToken) = getRemoteAccessToken(clientId)

            val requestUrl = "${extra.api}/admin/organizations/$orgCode/view?employee_limit=$limit&employee_skip=$skip&org_limit=0&org_skip=0&org_id=$orgId&access_token=$accessToken"
            logger.debug("【Request URL】:$requestUrl" )

            val response = webClient.getAbs(requestUrl).send().await()

            val responseBody = response.bodyAsJsonObject()
            if(response.resultSuccess()){
                logger.debug(responseBody.toString())
                val resultList = responseBody.getJsonArray("result")
                if(resultList.list.size > 0){
                    val resultJsonObject:JsonObject = resultList.getJsonObject(0)
                    val childrenList = resultJsonObject.getJsonArray("employees")
                    val children = mutableListOf<EmployeeDTO>()

                    childrenList.forEach{
                        children.add(EmployeeDTO.createInstanceFromJsomObject(it as JsonObject))
                    }
                    Future.succeededFuture(children)
                }else{
                    Future.failedFuture(responseBody.toString())
                }
            }else{
                logger.error(responseBody.toString())
                Future.failedFuture(responseBody.toString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }


    }

    private suspend fun getRemoteAccessToken(clientId: String): Pair<ISVClientExtraForWorkPlusDTO, String?> {
        val isvClient = isvClientApplication.queryClientByClientId(clientId).await()
        if (Objects.isNull(isvClient)) throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
        val extra = isvClient!!.extra as ISVClientExtraForWorkPlusDTO
        val accessToken = accessTokenApplication.requestRequestAccessToken(clientId = clientId).await()
        return Pair(extra, accessToken)
    }
}