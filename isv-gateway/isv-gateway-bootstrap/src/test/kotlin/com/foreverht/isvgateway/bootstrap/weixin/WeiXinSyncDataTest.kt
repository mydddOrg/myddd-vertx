package com.foreverht.isvgateway.bootstrap.weixin

import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class WeiXinSyncDataTest : AbstractRouteTest() {

    private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }
    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

    companion object {
      private lateinit var isvClientId:String
    }

    @Test
    fun testSyncWeiXinData(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val response = webClient.get(port,host,"/v1/sync/employeeAndOrganization/$isvClientId?orgCode=ww6dc4e6c2cbfbb62c")
                    .send()
                    .await()
                testContext.verify {
                    logger.debug(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }


    @BeforeEach
    fun prepareWorkWeiXinClient(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val webClient = WebClient.create(vertx)

                val workWeiXin = realWorkWeiXinISVClient()
                println(workWeiXin)
                val response = webClient.post(port, host,"/v1/clients")
                    .sendJsonObject(workWeiXin).await()

                testContext.verify {
                    println(response.bodyAsString())
                    Assertions.assertEquals(200,response.statusCode())
                }

                val bodyJson = response.bodyAsJsonObject()

                isvClientId = bodyJson.getString("clientId")

                saveSuiteTicketToLocal().await()
                saveAuthCodeToLocal().await()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun realWorkWeiXinISVClient() : JsonObject {
        return json {
            obj(
                "clientName" to UUID.randomUUID().toString(),
                "callback" to UUID.randomUUID().toString(),
                "extra" to obj(
                    "clientType" to "WorkWeiXin",
                    "corpId" to "wxeb3c9397ae2712a2",
                    "providerSecret" to "zFzZ5bKQ2vasR2hcgJMkvgped3KUNY-NxmHiIhONoy9z9mLWsxdjHQbABuY6c9_8",
                    "suiteId" to "wx2547800152da0539",
                    "suiteSecret" to "Leis38fRtRAA7tkQ2cffjmWhouKOnCH-PSy2KvCgLbg",
                    "token" to "YLzVPx0SW7eCUl",
                    "encodingAESKey" to "5nuHy1Cg6lw5FBIxi5HVchUpEv2qnxwlYxPBTmkVQvp"
                )
            )
        }
    }

    private suspend fun saveAuthCodeToLocal(): Future<Unit> {
        return try {
            val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/weixin/authCode/wx2547800152da0539/ww6dc4e6c2cbfbb62c")
                .send().await()
            if(response.statusCode() == 200){
                val body = response.bodyAsJsonObject()
                val isvAuthCodeDTO = ISVAuthCodeDTO(
                    suiteId = body.getString("suiteId"),
                    clientType = body.getString("clientType"),
                    authStatus = body.getString("authStatus"),
                    orgCode = body.getString("orgCode"),
                    domainId = body.getString("domainId"),
                    temporaryAuthCode = body.getString("temporaryAuthCode"),
                    permanentAuthCode = body.getString("permanentAuthCode")
                )

                isvAuthCodeApplication.createTemporaryAuthCode(authCode = isvAuthCodeDTO).await()
                isvAuthCodeApplication.toPermanent(authCode = isvAuthCodeDTO).await()

                Future.succeededFuture()
            }else{
                Future.failedFuture(response.bodyAsString())
            }
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



}