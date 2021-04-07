package cc.lingenliu.example.document.bootstrap.validation

import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.json.schema.common.dsl.Schemas
import org.myddd.vertx.web.router.AbstractValidationHandler

class DocumentValidationHandler: AbstractValidationHandler() {

    private val createDocumentSchema by lazy {
        Schemas.objectSchema()
            .requiredProperty("mediaId",Schemas.stringSchema())
            .requiredProperty("name",Schemas.stringSchema())
            .requiredProperty("documentType",Schemas.enumSchema("File","Dir"))
            .requiredProperty("md5",Schemas.stringSchema())
            .requiredProperty("suffix",Schemas.stringSchema())

    }

    fun createDocumentValidationHandler() : ValidationHandler {
        return ValidationHandler
            .builder(schemaParser)
            .body(Bodies.json(createDocumentSchema))
            .build()
    }

}