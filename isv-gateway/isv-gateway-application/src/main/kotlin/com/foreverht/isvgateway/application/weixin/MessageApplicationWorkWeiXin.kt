package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MessageApplication
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.resultSuccessForWorkWeiXin
import io.vertx.core.Future
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory

class MessageApplicationWorkWeiXin: AbstractApplication(),MessageApplication {

    private val workWeiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }

    companion object {
        private const val WORK_WEI_XIN_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message"
    }

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    override suspend fun sendMessage(isvAccessToken: String, message: MessageDTO): Future<Boolean> {
        return try {
            val (_,isvClientToken) = getAuthCode(isvAccessToken = isvAccessToken).await()

            val agentId = workWeiXinApplication.queryAgentId(corpAccessToken = isvClientToken.accessToken()).await()
            val requestBody = json {
                obj(
                    "touser" to message.toUserList.joinToString("|"),
                    "toparty" to message.toOrgList.joinToString("|"),
                    "agentid" to agentId,
                    "msgtype" to message.body.msgType,
                    message.body.weiXinBodyKey() to message.body.weiXinBodyValue()
                )
            }

            val response =  webClient.postAbs("$WORK_WEI_XIN_MESSAGE/send?access_token=${isvClientToken.accessToken()}")
                .sendJsonObject(requestBody)
                .await()

            if(response.resultSuccessForWorkWeiXin()){
                Future.succeededFuture(true)
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}