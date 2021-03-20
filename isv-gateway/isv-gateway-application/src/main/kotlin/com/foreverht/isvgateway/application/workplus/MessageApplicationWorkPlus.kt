package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.MessageApplication
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.api
import io.vertx.core.Future
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class MessageApplicationWorkPlus :AbstractApplicationWorkPlus(),MessageApplication {

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    companion object {
        private const val SEND_MESSAGE = "%s/apps/mbox?access_token=%s&source_type=NATIVE&for_all=%s"
    }

    override suspend fun sendMessage(isvAccessToken: String, message: MessageDTO): Future<Boolean> {
        return try {
            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val requestUrl = String.format(SEND_MESSAGE,isvClientToken.api(),isvClientToken.accessToken(),message.forAll)

            val requestBody = json {
                obj(
                    "type" to message.body.msgType,
                    "client_ids" to message.toUserList,
                    "scopes" to message.toOrgList,
                    "body" to message.body
                )
            }
            logger.debug(requestUrl)
            val response = webClient.postAbs(requestUrl)
                .sendJsonObject(requestBody)
                .await()
            logger.debug(requestBody.toString())

            logger.debug(response.bodyAsString())
            if(response.resultSuccess()){
                Future.succeededFuture(true)
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}