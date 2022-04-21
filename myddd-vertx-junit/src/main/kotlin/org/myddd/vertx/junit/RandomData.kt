package org.myddd.vertx.junit

import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.ioc.InstanceFactory

private val idGenerate by lazy { InstanceFactory.getInstance(IDGenerator::class.java) }
private val stringIDGenerator by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }


fun randomLong():Long {
    return idGenerate.nextId()
}

fun randomString():String {
    return stringIDGenerator.nextId()
}