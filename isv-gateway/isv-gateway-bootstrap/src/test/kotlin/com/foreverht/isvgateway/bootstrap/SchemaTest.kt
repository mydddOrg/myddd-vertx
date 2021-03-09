package com.foreverht.isvgateway.bootstrap

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.SchemaParser
import org.junit.jupiter.api.Test
import io.vertx.json.schema.SchemaRouterOptions

import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.common.dsl.Schemas.*
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*


@ExtendWith(VertxExtension::class)
class SchemaTest {

    companion object {
        
        private val logger = LoggerFactory.getLogger(SchemaTest::class.java)

        private lateinit var schemaRouter: SchemaRouter
        private lateinit var schemaParser: SchemaParser

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
            schemaParser = SchemaParser.createOpenAPI3SchemaParser(schemaRouter)
            testContext.completeNow()
        }

    }



    @Test
    fun testPostTokenSchema(vertx: Vertx,testContext: VertxTestContext){

        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val postTokenSchema = objectSchema()
                    .requiredProperty("clientId", stringSchema())
                    .requiredProperty("clientSecret", stringSchema())
                    .requiredProperty("grantType", enumSchema("client_credentials"))
                    .build(schemaParser)

                val validJson = JsonObject()
                    .put("clientId", UUID.randomUUID().toString())
                    .put("clientSecret",UUID.randomUUID().toString())
                    .put("grantType","client_credentials")

                postTokenSchema.validateAsync(validJson).await()

                try {
                    //error
                    val notValidJSON = JsonObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())

                    postTokenSchema.validateAsync(notValidJSON).await()
                }catch (e:Exception){
                    logger.error(e)
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                try {
                    //error
                    val notValidJSON = JsonObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("clientSecret",UUID.randomUUID().toString())
                        .put("grantType","password")


                    postTokenSchema.validateAsync(notValidJSON).await()
                }catch (e:Exception){
                    logger.error(e)
                    testContext.verify { Assertions.assertNotNull(e) }
                }

                testContext.completeNow()
            }catch (t:Throwable){
                testContext.failNow(t)
            }
        }

    }
}