package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class OrganizationValidationHandler : AbstractValidationHandler(){

    fun queryOrganizationValidation(): ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .queryParameter(Parameters.param("orgCode", Schemas.stringSchema()))
            .build()
    }
}