package org.myddd.vertx.file

import io.vertx.core.Future
import java.io.InputStream

interface FileDigest {

    suspend fun digest(path:String):Future<String>

    suspend fun digest(inputStream: InputStream):Future<String>
}