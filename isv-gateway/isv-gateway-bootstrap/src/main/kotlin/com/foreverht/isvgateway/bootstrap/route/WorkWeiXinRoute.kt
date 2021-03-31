package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.SyncDataApplication
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkWeiXinDTO
import com.foreverht.isvgateway.bootstrap.ISVClientErrorCode
import com.qq.weixin.mp.aes.AesException
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.AbstractRouter
import org.myddd.vertx.web.router.ext.singleQueryParam
import org.myddd.vertx.xml.AsyncXPathParse
import org.w3c.dom.Document

class WorkWeiXinRoute(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {

    init {
        processCallbackHelloRoute()
        processCallbackEventRoute()
    }

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }
    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
    private val logger by lazy { LoggerFactory.getLogger(WorkWeiXinRoute::class.java) }
    private val syncDataApplication by lazy { InstanceFactory.getInstance(SyncDataApplication::class.java) }

    companion object {
        private const val CLIENT_TYPE_WORK_WEI_XIN = "WorkWeiXin"
        private const val SUCCESS = "success"
    }

    private fun processCallbackHelloRoute(){
        createGetRoute("/$version/callback/weixin/:clientId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val weiXinClient = isvClientApplication.queryClientByClientId(clientId = clientId).await()
                        requireNotNull(weiXinClient){
                            "client_id找不到"
                        }

                        val sVerifyMsgSig = it.singleQueryParam("msg_signature")
                        val sVerifyTimeStamp = it.singleQueryParam("timestamp")
                        val sVerifyNonce = it.singleQueryParam("nonce")
                        val sVerifyEchoStr = it.singleQueryParam("echostr")


                        val extra = weiXinClient.extra as ISVClientExtraForWorkWeiXinDTO
                        val wept = WXBizMsgCrypt(extra.token, extra.encodingAESKey, extra.corpId)
                        val sEchoStr = wept.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, sVerifyEchoStr)
                        it.end(sEchoStr)

                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }


            }

        }
    }

    private fun processCallbackEventRoute(){
        createPostRoute("/$version/callback/weixin/:clientId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val weiXinClient = isvClientApplication.queryClientByClientId(clientId = clientId).await()
                        requireNotNull(weiXinClient){
                            "client_id找不到"
                        }

                        val content = it.bodyAsString

                        val sVerifyMsgSig = it.singleQueryParam("msg_signature")
                        val sVerifyTimeStamp = it.singleQueryParam("timestamp")
                        val sVerifyNonce = it.singleQueryParam("nonce")
                        val extra = weiXinClient.extra as ISVClientExtraForWorkWeiXinDTO


                        var wept = WXBizMsgCrypt(extra.token, extra.encodingAESKey, extra.suiteId)
                        var decryptXml:String
                        try {
                            decryptXml = wept.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, content)
                        }catch (e: AesException){
                            wept = WXBizMsgCrypt(extra.token, extra.encodingAESKey, extra.corpId)
                            decryptXml = wept.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, content)
                        }

                        val document = AsyncXPathParse.parseXml(vertx,decryptXml.byteInputStream()).await()

                        val infoType = AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/InfoType").await()

                        logger.info("[InfoType]:$infoType")
                        logger.info(decryptXml)

                        when(infoType.toLowerCase()){
                            "suite_ticket" -> processSuiteEvent(document).await()
                            "create_auth" -> processCreateAuth(clientId,document).await()
                            "cancel_auth" -> processCancelAuth(clientId,document).await()
                            "change_auth" -> processChangeAuth(clientId,document).await()
                            else -> {

                                logger.info(it.request().absoluteURI())
                                throw BusinessLogicException(ISVClientErrorCode.EVENT_TYPE_NOT_SUPPORT)
                            }
                        }

                        it.end(SUCCESS)
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }.consumes("text/xml")
    }


    private suspend fun processSuiteEvent(document: Document):Future<Unit> {
        return try {
            val suiteId = AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/SuiteId").await()
            val suiteTicket = AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/SuiteTicket").await()
            isvSuiteTicketApplication.saveSuiteTicket(ISVSuiteTicketDTO(suiteId = suiteId,suiteTicket = suiteTicket,clientType = CLIENT_TYPE_WORK_WEI_XIN)).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun processChangeAuth(clientId:String,document: Document):Future<Unit> {
        return try {
            val authCorpId =  AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/AuthCorpId").await()
            syncDataApplication.syncOrganization(clientId = clientId,domainId = CLIENT_TYPE_WORK_WEI_XIN,orgCode = authCorpId)
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun processCreateAuth(clientId:String,document: Document):Future<Unit> {
        return try {
            val suiteId = AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/SuiteId").await()
            val authCode =  AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/AuthCode").await()
            isvSuiteTicketApplication.activeAuthForWeiXin(clientId = clientId,authCode = authCode,suiteId = suiteId).await()

            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private fun processCancelAuth(clientId: String,document: Document):Future<Unit> {
        return try {
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}