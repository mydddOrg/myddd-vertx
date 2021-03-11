package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class EmployeeValidationHandler : AbstractValidationHandler() {

    fun queryEmployeeByIdValidationHandler():ValidationHandler{
        return ValidationHandler
            .builder(schemaParser)
            .pathParameter(Parameters.param("orgCode", Schemas.stringSchema()))
            .pathParameter(Parameters.param("employeeId", Schemas.stringSchema()))
            .queryParameter(Parameters.param("accessToken",Schemas.stringSchema()))
            .build()
    }

    fun queryBatchQueryEmployeeValidationHandler():ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .pathParameter(Parameters.param("orgCode", Schemas.stringSchema()))
            .queryParameter(Parameters.param("userIds", Schemas.stringSchema()))
            .queryParameter(Parameters.param("accessToken",Schemas.stringSchema()))
            .build()
    }

    fun searchEmployeesValidationHandler():ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .pathParameter(Parameters.param("orgCode", Schemas.stringSchema()))
            .queryParameter(Parameters.param("query", Schemas.stringSchema()))
            .queryParameter(Parameters.param("accessToken",Schemas.stringSchema()))
            .build()
    }
}