package com.foreverht.isvgateway.bootstrap.ext

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

fun <T> List<T>.mapperTo():JsonArray {
    val jsonArray = JsonArray()
    this.forEach {
        jsonArray.add(JsonObject.mapFrom(it))
    }
    return jsonArray
}