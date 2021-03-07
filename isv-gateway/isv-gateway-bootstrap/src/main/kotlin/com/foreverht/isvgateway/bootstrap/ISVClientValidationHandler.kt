package com.foreverht.isvgateway.bootstrap

import io.vertx.core.Vertx
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import org.myddd.vertx.ioc.InstanceFactory

class ISVClientValidationHandler {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val schemaRouter: SchemaRouter by lazy { SchemaRouter.create(vertx, SchemaRouterOptions())}

    private val schemaParser: SchemaParser by lazy { SchemaParser.createOpenAPI3SchemaParser(schemaRouter) }

}