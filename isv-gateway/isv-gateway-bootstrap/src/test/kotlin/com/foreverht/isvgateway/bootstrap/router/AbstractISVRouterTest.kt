package com.foreverht.isvgateway.bootstrap.router

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.bootstrap.AbstractRouteTest
import com.foreverht.isvgateway.bootstrap.route.AbstractISVRouter
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import java.util.*

class AbstractISVRouterTest : AbstractRouteTest() {

    companion object {

        private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java)}
        private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
        private var accessToken:String? = null


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
                val isvClientDTO = randomISVClient()
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

        private fun randomISVClient() : ISVClientDTO {
            val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
                clientId = UUID.randomUUID().toString(),
                clientSecret = UUID.randomUUID().toString(),
                domainId = UUID.randomUUID().toString(),
                api = UUID.randomUUID().toString(),
                ownerId = UUID.randomUUID().toString()
            )

            return ISVClientDTO(clientName = UUID.randomUUID().toString(),extra = isvClientExtraDTO,callback = UUID.randomUUID().toString())
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