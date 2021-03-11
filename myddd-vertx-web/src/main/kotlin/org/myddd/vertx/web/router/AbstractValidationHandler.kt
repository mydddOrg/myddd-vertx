package org.myddd.vertx.web.router

import io.vertx.core.Vertx
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import org.myddd.vertx.ioc.InstanceFactory

abstract class AbstractValidationHandler {

    val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    val schemaRouter: SchemaRouter by lazy { SchemaRouter.create(vertx, SchemaRouterOptions())}

    val schemaParser: SchemaParser by lazy { SchemaParser.createOpenAPI3SchemaParser(schemaRouter) }

}