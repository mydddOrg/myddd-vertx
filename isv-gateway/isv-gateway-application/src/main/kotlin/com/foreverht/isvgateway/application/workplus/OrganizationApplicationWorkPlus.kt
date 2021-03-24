package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.api
import com.foreverht.isvgateway.application.extention.resultSuccess
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class OrganizationApplicationWorkPlus : AbstractApplication(),OrganizationApplication {

    private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    override suspend fun queryOrganizationById(
        isvAccessToken:String,
        orgCode: String,
        orgId: String?
    ): Future<OrganizationDTO> {
        return try {
            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val requestUrl = "${isvClientToken.api()}/admin/organizations/$orgCode/view?employee_limit=0&org_limit=0&org_id=$orgId&access_token=${isvClientToken.accessToken()}"
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

    override suspend fun queryChildrenOrganizations(orgPageQueryDTO: OrgPageQueryDTO): Future<List<OrganizationDTO>> {
        return try {
            val isvClientToken = getRemoteAccessToken(orgPageQueryDTO.accessToken).await()

            val requestUrl = "${isvClientToken.api()}/admin/organizations/${orgPageQueryDTO.orgCode}/view?employee_limit=0&org_limit=${orgPageQueryDTO.limit}&org_skip=${orgPageQueryDTO.skip}&org_id=${orgPageQueryDTO.orgId}&access_token=${isvClientToken.accessToken()}"
            logger.debug("【Request URL】:$requestUrl")

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

    override suspend fun queryOrganizationEmployees(orgPageQueryDTO: OrgPageQueryDTO): Future<List<EmployeeDTO>> {
        return try {
            val isvClientToken = getRemoteAccessToken(orgPageQueryDTO.accessToken).await()

            val requestUrl = "${isvClientToken.api()}/admin/organizations/${orgPageQueryDTO.orgCode}/view?employee_limit=${orgPageQueryDTO.limit}&employee_skip=${orgPageQueryDTO.skip}&org_limit=0&org_skip=0&org_id=${orgPageQueryDTO.orgId}&access_token=${isvClientToken.accessToken()}"
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


}