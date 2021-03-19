package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.AppApplication
import com.foreverht.isvgateway.api.dto.AppDTO
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class AppApplicationWorkPlus :AbstractApplicationWorkPlus(),AppApplication {

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }


    override suspend fun getAdminList(isvAccessToken:String): Future<List<EmployeeDTO>> {
        return try {
            val (extra, accessToken) = getRemoteAccessToken(isvAccessToken).await()

            val requestUrl = "${extra.api}/apps/${extra.clientId}/admins?source_type=native&access_token=$accessToken"
            val response = webClient.getAbs(requestUrl).send().await()
            if(response.resultSuccess()){
                val bodyJson = response.bodyAsJsonObject()
                val result = bodyJson.getJsonObject("result")
                val childrenList = result.getJsonArray("records")
                val children = mutableListOf<EmployeeDTO>()

                childrenList.forEach{
                    children.add(EmployeeDTO.createInstanceFromJsomObject(it as JsonObject))
                }
                Future.succeededFuture(children)
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun getAppDetail(isvAccessToken: String): Future<AppDTO> {
        return try {
            val (extra, accessToken) = getRemoteAccessToken(isvAccessToken).await()
            val requestUrl = "${extra.api}/apps/${extra.clientId}?access_token=$accessToken"
            val response = webClient.getAbs(requestUrl).send().await()
            if(response.resultSuccess()){
                val bodyJson = response.bodyAsJsonObject()
                Future.succeededFuture(AppDTO.createInstanceFromJson(bodyJson.getJsonObject("result")))
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}