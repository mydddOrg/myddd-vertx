package com.foreverht.isvgateway.bootstrap.route

import com.qq.weixin.mp.aes.WXBizJsonMsgCrypt
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.AbstractRouter
import org.myddd.vertx.web.router.ext.singleQueryParam

class WorkWeiXinRoute(vertx: Vertx, router: Router) : AbstractRouter(vertx = vertx,router = router) {

    init {
      processWeiXinEvent()
    }

    companion object {
        private const val sToken = "YLzVPx0SW7eCUl"
        private const val sCorpID = "wxeb3c9397ae2712a2"
        private const val sEncodingAESKey = "5nuHy1Cg6lw5FBIxi5HVchUpEv2qnxwlYxPBTmkVQvp"

        private val logger by lazy { LoggerFactory.getLogger(WorkWeiXinRoute::class.java) }
    }

    private fun processWeiXinEvent(){
        createPostRoute("/$version/callback/weixin"){ route ->

            route.handler {
                logger.info(it.bodyAsString)
                val sVerifyMsgSig = it.singleQueryParam("msg_signature")
                val sVerifyTimeStamp = it.singleQueryParam("timestamp")
                val sVerifyNonce = it.singleQueryParam("nonce")

                val sVerifyEchoStr = it.bodyAsString
//                val wept = WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID)
//                val sEchoStr = wept.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, sVerifyEchoStr)

                it.end()
            }

        }
    }
}