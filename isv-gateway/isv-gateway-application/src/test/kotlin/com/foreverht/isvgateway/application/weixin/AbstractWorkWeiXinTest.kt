package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.RequestTokenDTO
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkWeiXinDTO
import com.foreverht.isvgateway.application.AccessTokenApplicationImpl
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeAll
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

abstract class AbstractWorkWeiXinTest: AbstractTest() {

    companion object {
        const val WORK_WEI_XIN = "WorkWeiXin"

        val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

        private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
        private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }
        val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplicationImpl::class.java) }

        lateinit var isvWorkWeiXinClientId:String
        lateinit var isvWorkWeiXinClientSecret:String

        lateinit var isvAccessToken:String

        @BeforeAll
        @JvmStatic
        fun prepareWorkWeiXinData(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    createWorkWeiXin().await()
                    saveSuiteTicketToLocal().await()

                    requestAccessToken().await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        private suspend fun requestAccessToken():Future<Unit>{
            return try {
                val requestTokenDTO = RequestTokenDTO(clientId = isvWorkWeiXinClientId, clientSecret = isvWorkWeiXinClientSecret,orgCode = "ww6dc4e6c2cbfbb62c")
                saveAuthCodeToLocal(webClient).await()

                val tokenDTO = accessTokenApplication.requestAccessToken(requestTokenDTO).await()
                isvAccessToken = tokenDTO.accessToken
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        private suspend fun saveSuiteTicketToLocal():Future<Unit>{
            return try {
                val url = "http://isvgateway.workplus.io:8080/v1/weixin/tickets/wx2547800152da0539"
                val response = webClient.getAbs(url).send().await()
                if(response.statusCode() == 200){

                    val body = response.bodyAsJsonObject()
                    val isvSuiteTicket = isvSuiteTicketApplication.saveSuiteTicket(
                        ISVSuiteTicketDTO(suiteId = body.getString("suiteId"), suiteTicket = body.getString("suiteTicket"), clientType = ISVClientType.WorkWeiXin.toString())
                    ).await()
                    if(Objects.isNull(isvSuiteTicket)){
                        Future.failedFuture("SUITE NOT FOUND")
                    }else{
                        Future.succeededFuture()
                    }
                }else{
                    Future.failedFuture("请求远程服务出错，未返回200状态:$url")
                }
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }


        private suspend fun createWorkWeiXin():Future<Unit>{
            return try {
                val extra = ISVClientExtraForWorkWeiXinDTO(
                    suiteId = "wx2547800152da0539",
                    suiteSecret = "Leis38fRtRAA7tkQ2cffjmWhouKOnCH-PSy2KvCgLbg",
                    corpId = "wxeb3c9397ae2712a2",
                    providerSecret = "zFzZ5bKQ2vasR2hcgJMkvgped3KUNY-NxmHiIhONoy9z9mLWsxdjHQbABuY6c9_8",
                    token = "YLzVPx0SW7eCUl",
                    encodingAESKey = "5nuHy1Cg6lw5FBIxi5HVchUpEv2qnxwlYxPBTmkVQvp"
                )
                val isvClient = ISVClientDTO(clientName = randomIDString.randomString(),callback = randomIDString.randomString(),extra = extra)
                val created = isvClientApplication.createISVClient(isvClientDTO = isvClient).await()
                isvWorkWeiXinClientId = created.clientId!!
                isvWorkWeiXinClientSecret = created.clientSecret!!
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }


        suspend fun saveAuthCodeToLocal(webClient: WebClient): Future<Unit> {
            return try {
                val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/weixin/authCode/wx2547800152da0539/ww6dc4e6c2cbfbb62c")
                    .send().await()
                if(response.statusCode() == 200){
                    val body = response.bodyAsJsonObject()
                    val isvAuthCode = ISVAuthCodeDTO(
                        suiteId = body.getString("suiteId"),
                        clientType = body.getString("clientType"),
                        authStatus = body.getString("authStatus"),
                        orgCode = body.getString("orgCode"),
                        domainId = body.getString("domainId"),
                        temporaryAuthCode = body.getString("temporaryAuthCode"),
                        permanentAuthCode = body.getString("permanentAuthCode")
                    )

                    isvAuthCodeApplication.createTemporaryAuthCode(authCode = isvAuthCode).await()
                    isvAuthCodeApplication.toPermanent(authCode = isvAuthCode).await()

                    Future.succeededFuture()
                }else{
                    Future.failedFuture(response.bodyAsString())
                }
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

    }



}