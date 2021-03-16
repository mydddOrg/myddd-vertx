package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.common.dsl.GenericSchemaBuilder
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class ISVClientValidationHandler : AbstractValidationHandler() {

    internal val extraForWorkPlus: ObjectSchemaBuilder? by lazy {
        Schemas.objectSchema()
            .requiredProperty("clientId",Schemas.stringSchema())
            .requiredProperty("clientSecret",Schemas.stringSchema())
            .requiredProperty("domainId",Schemas.stringSchema())
            .requiredProperty("api",Schemas.stringSchema())
            .requiredProperty("ownerId",Schemas.stringSchema())
            .requiredProperty("clientType",Schemas.enumSchema("WorkPlusISV","WorkPlusApp","WorkWeiXin"))
    }

    internal val createISVClientSchema: ObjectSchemaBuilder? by lazy {
        requireNotNull(extraForWorkPlus)


        Schemas.objectSchema()
            .requiredProperty("clientName", Schemas.stringSchema())
            .requiredProperty("callback",Schemas.stringSchema())
            .optionalProperty("description",Schemas.stringSchema())
            .requiredProperty("extra",Schemas.oneOf(
                extraForWorkPlus
            ))
    }

    internal val updateISVClientSchema : GenericSchemaBuilder? by lazy {
        Schemas.anyOf(
            Schemas.objectSchema().requiredProperty("clientName",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("callback",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("description",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("extra",Schemas.oneOf(
                extraForWorkPlus
            ))
        )
    }

    internal val requestAccessTokenSchema by lazy {
        Schemas.objectSchema()
            .requiredProperty("clientId",Schemas.stringSchema())
            .requiredProperty("clientSecret",Schemas.stringSchema())
            .requiredProperty("grantType",Schemas.enumSchema("client_credentials"))
    }

    internal val refreshTokenSchema by lazy {
        Schemas.objectSchema()
            .requiredProperty("clientId",Schemas.stringSchema())
            .requiredProperty("refreshToken",Schemas.stringSchema())
    }

    internal val resetClientSecretSchema by lazy {
        Schemas.objectSchema()
            .requiredProperty("clientId",Schemas.stringSchema())
            .requiredProperty("clientSecret",Schemas.stringSchema())
    }

    fun createISVClientValidation(): ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(createISVClientSchema))
            .build()
    }

    fun updateISVClientValidation(): ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(updateISVClientSchema))
            .build()
    }

    fun requestAccessTokenValidation(): ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(requestAccessTokenSchema))
            .build()
    }

    fun refreshTokenValidation() : ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(refreshTokenSchema))
            .build()
    }

    fun resetSecretValidation() : ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(resetClientSecretSchema))
            .build()
    }

}