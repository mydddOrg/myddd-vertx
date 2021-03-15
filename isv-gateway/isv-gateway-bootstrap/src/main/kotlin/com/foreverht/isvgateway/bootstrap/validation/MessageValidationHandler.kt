package com.foreverht.isvgateway.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class MessageValidationHandler : AbstractValidationHandler() {

    companion object {
        private const val TEXT_MESSAGE_BODY = "TEXT_MESSAGE_BODY"
        private const val IMAGE_MESSAGE_BODY = "IMAGE_MESSAGE_BODY"
        private const val VOICE_MESSAGE_BODY = "VOICE_MESSAGE_BODY"
        private const val FILE_MESSAGE_BODY = "FILE_MESSAGE_BODY"

    }

    internal val testMessageBody: ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.constSchema("TEXT"))
            .requiredProperty("content",Schemas.stringSchema())
            .alias(TEXT_MESSAGE_BODY)
    }

    internal val imageMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.constSchema("IMAGE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("content",Schemas.stringSchema())
            .requiredProperty("height",Schemas.numberSchema())
            .requiredProperty("width",Schemas.numberSchema())
            .optionalProperty("isGif",Schemas.booleanSchema())
            .alias(IMAGE_MESSAGE_BODY)
    }

    internal val voiceMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.constSchema("VOICE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("duration",Schemas.intSchema())
            .alias(VOICE_MESSAGE_BODY)
    }


    internal val fileMessageBody:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .requiredProperty("msgType",Schemas.constSchema("FILE"))
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("name",Schemas.stringSchema())
            .requiredProperty("size",Schemas.intSchema())
            .alias(FILE_MESSAGE_BODY)

    }

    internal val messageSchema:ObjectSchemaBuilder by lazy {
        Schemas.objectSchema()
            .optionalProperty("forAll",Schemas.booleanSchema())
            .optionalProperty("toUserList",Schemas.arraySchema())
            .optionalProperty("toOrgList",Schemas.arraySchema())
            .requiredProperty("body",Schemas.anyOf(
                Schemas.refToAlias(TEXT_MESSAGE_BODY),
                Schemas.refToAlias(IMAGE_MESSAGE_BODY),
                Schemas.refToAlias(VOICE_MESSAGE_BODY),
                Schemas.refToAlias(FILE_MESSAGE_BODY)
            )
            )
    }


    init {
        testMessageBody.build(schemaParser)
        imageMessageBody.build(schemaParser)
        voiceMessageBody.build(schemaParser)
        fileMessageBody.build(schemaParser)
        messageSchema.build(schemaParser)
    }

    fun messageValidationHandler():ValidationHandler{
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(messageSchema))
            .build()
    }


}