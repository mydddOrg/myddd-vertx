package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.ioc.InstanceFactory

object UserRouterValidation {

    private val logger = LoggerFactory.getLogger(UserRouterValidation::class.java)

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val schemaRouter: SchemaRouter by lazy { SchemaRouter.create(vertx, SchemaRouterOptions())}

    private val schemaParser: SchemaParser by lazy { SchemaParser.createOpenAPI3SchemaParser(schemaRouter) }

    private val postUserSchema by lazy {
        Schemas.objectSchema()
            .requiredProperty("userId", Schemas.stringSchema())
    }

    fun postUserValidation():ValidationHandler{
       return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(postUserSchema))
            .build()
    }

}