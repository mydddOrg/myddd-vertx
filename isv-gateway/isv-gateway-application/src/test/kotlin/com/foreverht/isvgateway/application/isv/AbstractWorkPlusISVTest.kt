package com.foreverht.isvgateway.application.isv

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.api.dto.RequestTokenDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusISVDTO
import com.foreverht.isvgateway.application.W6SBossApplication
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

@ExtendWith(VertxExtension::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
abstract class AbstractWorkPlusISVTest : AbstractTest() {

    companion object {

        lateinit var suiteTicket:String

        lateinit var isvClientId:String

        lateinit var isvClientSecret:String

        val logger: Logger by lazy { LoggerFactory.getLogger(AbstractWorkPlusISVTest::class.java) }

        private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

        private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }

        private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }

        val w6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }
        val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java) }

        const val ORG_CODE = "2975ff5f83a34f458280fd25fbd3a356"
        const val DOMAIN_ID = "workplus"
        lateinit var isvAccessToken:String

        @BeforeAll
        @JvmStatic
        @Order(1)
        fun anotherBeforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val webClient = WebClient.create(vertx)


                    saveSuiteTicketToLocal(webClient = webClient).await()
                    saveTmpAuthCodeToLocal(webClient = webClient).await()

                    suiteTicket = querySuiteTicket(webClient).await()
                    testContext.verify {
                        Assertions.assertNotNull(suiteTicket)
                    }

                    val w6sISVClient = isvClientApplication.createISVClient(realW6SISVClient()).await()
                    testContext.verify {
                        Assertions.assertNotNull(w6sISVClient)
                        Assertions.assertEquals(ISVClientType.WorkPlusISV.toString(),w6sISVClient.extra.clientType)
                    }
                    isvClientId = w6sISVClient.clientId!!
                    isvClientSecret = w6sISVClient.clientSecret!!

                    requestAccessToken(testContext).await()

                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        suspend fun requestAccessToken(testContext: VertxTestContext):Future<Unit>{
           return try {
                try {
                    with(w6SBossApplication) { requestApiAccessToken(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await() }
                }catch (t:Throwable){
                    Assertions.assertNotNull(t)
                }

                val permanent = w6SBossApplication.requestPermanentCode(clientId = isvClientId,domainId = DOMAIN_ID,orgCode = ORG_CODE).await()
                testContext.verify {
                    Assertions.assertNotNull(permanent)
                    Assertions.assertNotNull(permanent.permanentAuthCode)
                }

                val requestTokenDTO = RequestTokenDTO(clientId = isvClientId, clientSecret = isvClientSecret,
                    domainId = DOMAIN_ID,orgCode = ORG_CODE
                )

                val tokenDTO = accessTokenApplication.requestAccessToken(requestTokenDTO = requestTokenDTO).await()
                testContext.verify { Assertions.assertNotNull(tokenDTO) }

                val query = accessTokenApplication.queryClientByAccessToken(isvAccessToken = tokenDTO.accessToken).await()
                testContext.verify { Assertions.assertNotNull(query) }

                isvAccessToken = tokenDTO.accessToken
               Future.succeededFuture()
           }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }



        private suspend fun querySuiteTicket(webClient: WebClient):Future<String>{
            return try {
                val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/w6s/tickets/njVwg-pgkeI5nK11iAdduH")
                    .send().await()
                if(response.statusCode() == 200){
                    val body = response.bodyAsJsonObject()
                    logger.debug("suiteTicket:${body.getString("suiteTicket")}")
                    Future.succeededFuture(body.getString("suiteTicket"))
                }else{
                    Future.failedFuture(response.bodyAsString())
                }
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        private fun realW6SISVClient() : ISVClientDTO {
            val isvClientExtraDTO = ISVClientExtraForWorkPlusISVDTO(
                suiteKey = "njVwg-pgkeI5nK11iAdduH",
                suiteSecret = "o0jF8HfNXNYE53o3kV22Vcag2oejnM1n",
                vendorKey = "k2n23vwy0gEKxpS_Bb237h",
                token = "KSbiWeOKpLQeyyVuJUT2X6JOM2iqlWAgosk0d0xXIEL",
                encryptSecret = "CoOREEhw6KPCAyfIRLqVFyysEim0dUkWpC5rmDKaLYR",
                isvApi = "http://test248.workplus.io/v1/isv",
                appId = "Pu-xt6AREHB67AznU9ReDd"
            )

            return ISVClientDTO(clientName = UUID.randomUUID().toString(),extra = isvClientExtraDTO,callback = UUID.randomUUID().toString())
        }

        suspend fun saveTmpAuthCodeToLocal(webClient: WebClient):Future<Unit>{
            return try {
                val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/w6s/authCode/pernament/njVwg-pgkeI5nK11iAdduH/2975ff5f83a34f458280fd25fbd3a356")
                    .send().await()
                if(response.statusCode() == 200){
                    val body = response.bodyAsJsonObject()
                    val isvAuthCode = ISVAuthCodeDTO(
                        suiteId = body.getString("suiteId"),
                        clientType = body.getString("clientType"),
                        authStatus = body.getString("authStatus"),
                        orgCode = body.getString("orgCode"),
                        domainId = body.getString("domainId"),
                        temporaryAuthCode = body.getString("temporaryAuthCode")
                    )

                    logger.info("temporaryAuthCode:${isvAuthCode.temporaryAuthCode}")
                    isvAuthCodeApplication.createTemporaryAuthCode(authCode = isvAuthCode).await()
                    Future.succeededFuture()
                }else{
                    Future.failedFuture(response.bodyAsString())
                }
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun saveSuiteTicketToLocal(webClient: WebClient):Future<Unit>{
            return try {
                val response = webClient.getAbs("http://isvgateway.workplus.io:8080/v1/w6s/tickets/njVwg-pgkeI5nK11iAdduH")
                    .send().await()
                if(response.statusCode() == 200){
                    val body = response.bodyAsJsonObject()
                    val isvSuiteTicket = isvSuiteTicketApplication.saveSuiteTicket(ISVSuiteTicketDTO(
                        suiteId = body.getString("suiteId"),
                        suiteTicket = body.getString("suiteTicket"),
                        clientType = ISVClientType.WorkPlusISV.toString()
                    )).await()
                    if(Objects.isNull(isvSuiteTicket)){
                        Future.failedFuture("SUITE NOT FOUND")
                    }else{
                        Future.succeededFuture()
                    }
                }else{
                    Future.failedFuture(response.bodyAsString())
                }
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

    }




}