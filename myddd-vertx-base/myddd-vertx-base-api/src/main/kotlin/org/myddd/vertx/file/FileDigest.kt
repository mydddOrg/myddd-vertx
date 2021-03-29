package org.myddd.vertx.file

import io.vertx.core.Future

interface FileDigest {

    suspend fun digest(path:String):Future<String>

}