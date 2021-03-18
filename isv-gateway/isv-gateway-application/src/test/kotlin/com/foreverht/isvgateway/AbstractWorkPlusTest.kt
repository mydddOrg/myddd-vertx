package com.foreverht.isvgateway

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.application.workplus.AccessTokenApplicationWorkPlusTest
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.myddd.vertx.ioc.InstanceFactory

abstract class AbstractWorkPlusTest : AbstractTest() {

    companion object {

        val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java,"WorkPlusApp") }

        val logger: Logger = LoggerFactory.getLogger(AbstractWorkPlusTest::class.java)

        private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

        const val api = "http://test248.workplus.io/api4/v1"

        const val domainId = "workplus"

        const val clientId = "02018e570da2f42bf598d2f5628183d158e22a72"

        const val clientSecret = "63d3237269214272be13fbab7da791f3"

        const val ownerId = "2975ff5f83a34f458280fd25fbd3a356"

        lateinit var isvClientId:String

        val webClient: WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

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

        @BeforeAll
        @JvmStatic
        fun anotherBeforeAll(vertx: Vertx, testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val created = isvClientApplication.createISVClient(realISVClient()).await()

                    testContext.verify {
                        Assertions.assertNotNull(created)
                        Assertions.assertNotNull(created.clientId)
                        Assertions.assertNotNull(created.clientSecret)
                    }

                    created.clientId?.also { isvClientId = it }

                    testContext.completeNow()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
            }
        }

    }

}