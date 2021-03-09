package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.core.Vertx
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.GenericSchemaBuilder
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.ioc.InstanceFactory

object ISVClientValidationHandler {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val schemaRouter: SchemaRouter by lazy { SchemaRouter.create(vertx, SchemaRouterOptions())}

    val schemaParser: SchemaParser by lazy { SchemaParser.createOpenAPI3SchemaParser(schemaRouter) }

    private const val ALIAS_EXTRA_FOR_WORKPLUS = "extraForWorkPlus"

    internal val extraForWorkPlus: ObjectSchemaBuilder? by lazy {
        Schemas.objectSchema()
            .requiredProperty("clientId",Schemas.stringSchema())
            .requiredProperty("clientSecret",Schemas.stringSchema())
            .requiredProperty("domainId",Schemas.stringSchema())
            .requiredProperty("api",Schemas.stringSchema())
            .requiredProperty("ownerId",Schemas.stringSchema())
            .requiredProperty("clientType",Schemas.enumSchema("WorkPlusISV","WorkPlusApp","WorkWeiXin"))

            .alias(ALIAS_EXTRA_FOR_WORKPLUS)
    }

    internal val createISVClientSchema: ObjectSchemaBuilder? by lazy {
        requireNotNull(extraForWorkPlus)


        Schemas.objectSchema()
            .requiredProperty("clientName", Schemas.stringSchema())
            .requiredProperty("callback",Schemas.stringSchema())
            .optionalProperty("description",Schemas.stringSchema())
            .requiredProperty("extra",Schemas.oneOf(Schemas.refToAlias(ALIAS_EXTRA_FOR_WORKPLUS)))
    }

    internal val updateISVClientSchema : GenericSchemaBuilder? by lazy {
        Schemas.anyOf(
            Schemas.objectSchema().requiredProperty("clientName",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("callback",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("description",Schemas.stringSchema()),
            Schemas.objectSchema().requiredProperty("extra",Schemas.oneOf(Schemas.refToAlias(ALIAS_EXTRA_FOR_WORKPLUS)))
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

    init {
        extraForWorkPlus?.build(schemaParser)
        createISVClientSchema?.build(schemaParser)
        updateISVClientSchema?.build(schemaParser)
        requestAccessTokenSchema?.build(schemaParser)
        refreshTokenSchema?.build(schemaParser)
        resetClientSecretSchema?.build(schemaParser)
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