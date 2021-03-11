package com.foreverht.isvgateway.bootstrap.router

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import com.foreverht.isvgateway.bootstrap.route.AbstractISVRouter
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import java.util.*

open class AbstractISVRouterTest : AbstractRouteTest() {

    companion object {

        private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java)}
        private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

        var accessToken:String? = null

        val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

        @BeforeAll
        @JvmStatic
        fun preparedAccessToken(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    startVerticle(vertx,testContext).await()
                    createISVClientAndRequestAccessToken(vertx,testContext).await()
                }catch (t:Throwable){
                    testContext.completeNow()
                }
                testContext.completeNow()
            }
        }

        private suspend fun createISVClientAndRequestAccessToken(vertx: Vertx,testContext: VertxTestContext):Future<Unit>{
            return try {
                val isvClientDTO = realISVClient()
                val created = isvClientApplication.createISVClient(isvClientDTO).await()

                var userDTO = databaseOAuth2Application.requestClientToken(created.clientId!!,created.clientSecret!!).await()
                testContext.verify {
                    Assertions.assertNotNull(userDTO)
                    Assertions.assertFalse(userDTO!!.expired())
                }

                val clientId = databaseOAuth2Application.queryValidClientIdByAccessToken(userDTO!!.tokenDTO!!.accessToken).await()
                accessToken = userDTO!!.tokenDTO!!.accessToken
                testContext.verify { Assertions.assertNotNull(clientId) }

                Future.succeededFuture(Unit)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        const val api = "http://test248.workplus.io/api4/v1"

        const val domainId = "workplus"

        const val clientId = "02018e570da2f42bf598d2f5628183d158e22a72"

        const val clientSecret = "63d3237269214272be13fbab7da791f3"

        const val ownerId = "2975ff5f83a34f458280fd25fbd3a356"

        const val orgId = "aHexITjYkEurKyyxpKMgFh"

        lateinit var isvClientId:String

        private fun realISVClient() : ISVClientDTO {
            val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
                clientId = clientId,
                clientSecret = clientSecret,
                domainId = domainId,
                api = api,
                ownerId = ownerId
            )

            return ISVClientDTO(clientName = "WorkPlus Test App",extra = isvClientExtraDTO,callback = api)
        }

    }

    @Test
    fun test(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val testRoute = object : AbstractISVRouter(vertx = vertx,router = Router.router(vertx)){}
                val organizationApplication = testRoute.getOrganizationApplication(accessToken!!).await()
                testContext.verify { Assertions.assertNotNull(organizationApplication) }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}