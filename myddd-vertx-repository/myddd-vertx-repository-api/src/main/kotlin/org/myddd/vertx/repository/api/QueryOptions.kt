package org.myddd.vertx.repository.api

import io.vertx.core.json.JsonObject

data class QueryOptions(val fields:JsonObject = JsonObject(),
                        val sort:JsonObject = JsonObject(),
                        val limit:Int = -1,
                        val skip:Int = 0,
                        var batchSize:Int = 20,
                        val hint:String = "")