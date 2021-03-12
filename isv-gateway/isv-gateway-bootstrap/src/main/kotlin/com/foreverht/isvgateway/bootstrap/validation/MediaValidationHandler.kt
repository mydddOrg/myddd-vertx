package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class MediaValidationHandler: AbstractValidationHandler() {

    fun downloadMediaValidationHandler() : ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .pathParameter(Parameters.param("mediaId", Schemas.stringSchema()))
            .build()
    }
}