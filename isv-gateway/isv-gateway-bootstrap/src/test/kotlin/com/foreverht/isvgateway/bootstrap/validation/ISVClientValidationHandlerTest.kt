package com.foreverht.isvgateway.bootstrap.validation

import com.foreverht.isvgateway.domain.ISVClientType
import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import java.util.*

@ExtendWith(VertxExtension::class)
class ISVClientValidationHandlerTest {

    private val logger by lazy { LoggerFactory.getLogger(ISVClientValidationHandlerTest::class.java) }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(vertx)
                }
            })))

            testContext.completeNow()
        }
    }

    @Test
    fun testExtraForWorkPlusValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val extraForWorkPlusValidation = ISVClientValidationHandler().extraForWorkPlus?.build(ISVClientValidationHandler().schemaParser)

                val extraForWorkPlusJson = JsonObject()
                    .put("clientId", UUID.randomUUID().toString())
                    .put("clientSecret",UUID.randomUUID().toString())
                    .put("api",UUID.randomUUID().toString())
                    .put("domainId",UUID.randomUUID().toString())
                    .put("ownerId",UUID.randomUUID().toString())
                    .put("clientType",ISVClientType.WorkPlusApp.toString())


                extraForWorkPlusValidation?.validateAsync(extraForWorkPlusJson)?.await()

                try {
                    val notValidExtraForWorkPlusJson = JsonObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())
                        .put("domainId",UUID.randomUUID().toString())

                    extraForWorkPlusValidation?.validateAsync(notValidExtraForWorkPlusJson)?.await()

                }catch (t:Throwable){
                    logger.error(t)
                }

                testContext.completeNow()

            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }
    }

    @Test
    fun testCreateISVClientSchema(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val createISVClientValidation = ISVClientValidationHandler().createISVClientSchema?.build(ISVClientValidationHandler().schemaParser)

                val extraForWorkPlusJson = JsonObject()
                    .put("clientId", UUID.randomUUID().toString())
                    .put("clientSecret",UUID.randomUUID().toString())
                    .put("api",UUID.randomUUID().toString())
                    .put("domainId",UUID.randomUUID().toString())
                    .put("ownerId",UUID.randomUUID().toString())
                    .put("clientType","WorkPlusApp")

                val createISVClientJson = JsonObject()
                    .put("clientName",UUID.randomUUID().toString())
                    .put("callback",UUID.randomUUID().toString())
                    .put("extra",extraForWorkPlusJson)

                createISVClientValidation?.validateAsync(createISVClientJson)?.await()

                try {

                    val notValidExtraForWorkPlusJson = JsonObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())
                        .put("domainId",UUID.randomUUID().toString())
                        .put("clientType","ABC")

                    val notValidCreateISVClientJson = JsonObject()
                        .put("clientName",UUID.randomUUID().toString())
                        .put("callback",UUID.randomUUID().toString())
                        .put("extra",notValidExtraForWorkPlusJson)

                    createISVClientValidation?.validateAsync(notValidCreateISVClientJson)?.await()

                }catch (t:Throwable){
                    logger.error(t)
                }

                try {

                    val notValidExtraForWorkPlusJson = JsonObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())
                        .put("domainId",UUID.randomUUID().toString())
                        .put("clientType","WorkPlusApp")

                    val notValidCreateISVClientJson = JsonObject()
                        .put("clientName",UUID.randomUUID().toString())
                        .put("callback",UUID.randomUUID().toString())
                        .put("extra",notValidExtraForWorkPlusJson)

                    createISVClientValidation?.validateAsync(notValidCreateISVClientJson)?.await()

                }catch (t:Throwable){
                    logger.error(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateISVClient(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val updateISVClientValidation = ISVClientValidationHandler().updateISVClientSchema?.build(ISVClientValidationHandler().schemaParser)


                val createISVClientJson = JsonObject()
                    .put("clientName",UUID.randomUUID().toString())

                updateISVClientValidation?.validateAsync(createISVClientJson)?.await()

                try {
                    val notValidCreateISVClientJson = JsonObject()
                    updateISVClientValidation?.validateAsync(notValidCreateISVClientJson)?.await()
                }catch (t:Throwable){
                    logger.error(t)
                }

                try {

                    val notValidCreateISVClientJson = JsonObject()
                        .put("a",UUID.randomUUID().toString())

                    updateISVClientValidation?.validateAsync(notValidCreateISVClientJson)?.await()

                }catch (t:Throwable){
                    logger.error(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }

    @Test
    fun testRequestAccessTokenValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val requestAccessTokenValidation = ISVClientValidationHandler().requestAccessTokenSchema.build(ISVClientValidationHandler().schemaParser)
                val requestJsonObject = JsonObject()
                    .put("clientId",UUID.randomUUID().toString())
                    .put("clientSecret",UUID.randomUUID().toString())
                    .put("grantType","client_credentials")

                requestAccessTokenValidation.validateAsync(requestJsonObject).await()

                try {
                    val notValidJSON = JsonObject()
                        .put("clientId",UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())
                        .put("grantType","ABC")

                    requestAccessTokenValidation.validateAsync(notValidJSON).await()

                }catch (e:Exception){
                    logger.error(e)
                }

                try {
                    val notValidJSON = JsonObject()
                        .put("clientId",UUID.randomUUID().toString())
                        .put("grantType","client_credentials")

                    requestAccessTokenValidation.validateAsync(notValidJSON).await()

                }catch (e:Exception){
                    logger.error(e)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testRefreshTokenValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val refreshTokenValidation = ISVClientValidationHandler().refreshTokenSchema.build(ISVClientValidationHandler().schemaParser)
                val requestJsonObject = JsonObject()
                    .put("clientId",UUID.randomUUID().toString())
                    .put("refreshToken",UUID.randomUUID().toString())

                refreshTokenValidation.validateAsync(requestJsonObject).await()

                try {
                    val notValidJson = JsonObject()
                        .put("clientId",UUID.randomUUID().toString())
                    refreshTokenValidation.validateAsync(notValidJson).await()
                    testContext.failNow("不可能运行到这")
                }catch (t:Throwable){
                    logger.error(t)
                }

                try {
                    refreshTokenValidation.validateAsync(JsonObject()).await()
                    testContext.failNow("不可能运行到这")

                }catch (t:Throwable){
                    logger.error(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }

            testContext.completeNow()
        }
    }


    @Test
    fun testResetSecretValidation(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val resetSecretValidation = ISVClientValidationHandler().resetClientSecretSchema.build(ISVClientValidationHandler().schemaParser)
                val requestJsonObject = JsonObject()
                    .put("clientId",UUID.randomUUID().toString())
                    .put("clientSecret",UUID.randomUUID().toString())

                resetSecretValidation.validateAsync(requestJsonObject).await()

                try {
                    val notValidJson = JsonObject()
                        .put("clientId",UUID.randomUUID().toString())
                    resetSecretValidation.validateAsync(notValidJson).await()
                    testContext.failNow("不可能运行到这")
                }catch (t:Throwable){
                    logger.error(t)
                }

                try {
                    resetSecretValidation.validateAsync(JsonObject()).await()
                    testContext.failNow("不可能运行到这")

                }catch (t:Throwable){
                    logger.error(t)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}