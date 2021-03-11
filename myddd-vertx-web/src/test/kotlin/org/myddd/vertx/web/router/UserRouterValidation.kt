package org.myddd.vertx.web.router

import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.common.dsl.Schemas

class UserRouterValidation : AbstractValidationHandler() {

    private val logger = LoggerFactory.getLogger(UserRouterValidation::class.java)

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