package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.EmployeeApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class EmployeeApplicationWorkPlus :AbstractApplicationWorkPlus(),EmployeeApplication {

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    private val logger:Logger by lazy { LoggerFactory.getLogger(EmployeeApplicationWorkPlus::class.java) }

    companion object {
        private const val API_BATCH_QUERY_EMPLOYEE = "%s/admin/organizations/%s/employees?access_token=%s&matching=true&query=%s"
        private const val API_QUERY_EMPLOYEE = "%s/admin/organizations/%s/employees?access_token=%s&type=username&query=%s"
    }

    override suspend fun queryEmployeeById(clientId: String, orgCode:String,userId: String): Future<EmployeeDTO> {
        return try {
            val employeeDTOList = batchQueryEmployeeByIds(clientId = clientId,orgCode = orgCode, arrayListOf(userId)).await()
            Future.succeededFuture(employeeDTOList[0])
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun batchQueryEmployeeByIds(clientId: String, orgCode: String, userIdList: List<String>): Future<List<EmployeeDTO>> {
        return try {
            require(userIdList.isNotEmpty())

            val (extra, accessToken) = getRemoteAccessToken(clientId)
            val url = String.format(API_BATCH_QUERY_EMPLOYEE,extra.api,orgCode,accessToken,userIdList.joinToString(","))
            logger.debug("【Request URL】:$url")
            val response = webClient.getAbs(url).send().await()
            val bodyJson = response.bodyAsJsonObject()
            if(response.resultSuccess()){
                val result = bodyJson.getJsonArray("result")
                val employeeList:MutableList<EmployeeDTO> = mutableListOf()
                result.forEach { employeeList.add(EmployeeDTO.createInstanceFromJsomObject(it as JsonObject)) }
                Future.succeededFuture(employeeList)
            }else{
                Future.failedFuture(bodyJson.toString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun searchEmployees(clientId: String, orgCode: String, query: String): Future<List<EmployeeDTO>> {
        return try {

            check(query.isNotBlank())

            val (extra, accessToken) = getRemoteAccessToken(clientId)
            val url = String.format(API_QUERY_EMPLOYEE,extra.api,orgCode,accessToken,query)
            logger.debug("【Request URL】:$url")
            val response = webClient.getAbs(url).send().await()
            val bodyJson = response.bodyAsJsonObject()
            if(response.resultSuccess()){
                val result = bodyJson.getJsonArray("result")
                val employeeList:MutableList<EmployeeDTO> = mutableListOf()
                result.forEach { employeeList.add(EmployeeDTO.createInstanceFromJsomObject(it as JsonObject)) }
                Future.succeededFuture(employeeList)
            }else{
                Future.failedFuture(bodyJson.toString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}