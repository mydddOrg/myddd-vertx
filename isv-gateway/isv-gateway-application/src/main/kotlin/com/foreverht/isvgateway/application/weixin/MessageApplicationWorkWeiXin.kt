package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MessageApplication
import com.foreverht.isvgateway.api.dto.message.AbstractMessageBody
import com.foreverht.isvgateway.api.dto.message.MessageDTO
import com.foreverht.isvgateway.api.dto.message.body.FileMessageBody
import com.foreverht.isvgateway.api.dto.message.body.ImageMessageBody
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.WorkWeiXinApplication
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.resultSuccessForWorkWeiXin
import com.foreverht.isvgateway.domain.ISVErrorCode
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaErrorCode
import java.util.*

class MessageApplicationWorkWeiXin: AbstractApplication(),MessageApplication {

    private val workWeiXinApplication by lazy { InstanceFactory.getInstance(WorkWeiXinApplication::class.java) }

    companion object {
        private const val WORK_WEI_XIN_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message"
    }

    private val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    override suspend fun sendMessage(isvAccessToken: String, message: MessageDTO): Future<Boolean> {
        return try {
            if(!message.body.supportWeiXin()) throw BusinessLogicException(ISVErrorCode.MESSAGE_TYPE_NOT_SUPPORT)

            val (_,isvClientToken) = getAuthCode(isvAccessToken = isvAccessToken).await()

            val agentId = workWeiXinApplication.queryAgentId(corpAccessToken = isvClientToken.accessToken()).await()

            val requestBody = json {
                obj(
                    "touser" to message.toUserList.joinToString("|"),
                    "toparty" to message.toOrgList.joinToString("|"),
                    "agentid" to agentId,
                    "msgtype" to message.body.msgType,
                    message.body.weiXinBodyKey() to bodyValue(message,isvClientToken.accessToken()).await()
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

    private suspend fun bodyValue(message: MessageDTO,corpAccessToken:String):Future<JsonObject>{
        return try {
            val value = when (message.body.msgType){
                AbstractMessageBody.IMAGE_MSG_TYPE -> {
                    val body = message.body as ImageMessageBody
                    val weiXinMediaIdd = workWeiXinApplication.uploadResourceToWeiXinTmpMedia(mediaId = body.mediaId,corpAccessToken = corpAccessToken).await()
                    body.weiXinBodyValue(mediaId = weiXinMediaIdd)
                }
                AbstractMessageBody.FILE_MSG_TYPE -> {
                    val body = message.body as FileMessageBody
                    val weiXinMediaIdd = workWeiXinApplication.uploadResourceToWeiXinTmpMedia(mediaId = body.mediaId,corpAccessToken = corpAccessToken).await()
                    body.weiXinBodyValue(mediaId = weiXinMediaIdd)
                }
                else -> message.body.weiXinBodyValue()
            }
            Future.succeededFuture(value)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}