package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class MessageValidationHandler : AbstractValidationHandler() {

    internal val textMessageBody: ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.enumSchema("TEXT"))
            .requiredProperty("content",Schemas.stringSchema())
    }

    internal val imageMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.enumSchema("IMAGE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("content",Schemas.stringSchema())
            .requiredProperty("height",Schemas.numberSchema())
            .requiredProperty("width",Schemas.numberSchema())
    }

    internal val voiceMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.enumSchema("VOICE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("duration",Schemas.intSchema())
    }


    internal val fileMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.enumSchema("FILE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("name",Schemas.stringSchema())
            .requiredProperty("size",Schemas.intSchema())

    }

    internal val messageSchema:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .optionalProperty("forAll",Schemas.booleanSchema())
            .optionalProperty("toUserList",Schemas.arraySchema())
            .optionalProperty("toOrgList",Schemas.arraySchema())
            .requiredProperty("body",Schemas.oneOf(
                imageMessageBody,
                fileMessageBody,
                voiceMessageBody,
                textMessageBody
            )
            )
    }


    fun messageValidationHandler():ValidationHandler{
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(messageSchema))
            .build()
    }


}