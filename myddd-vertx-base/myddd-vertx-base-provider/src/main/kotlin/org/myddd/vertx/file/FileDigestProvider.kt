package org.myddd.vertx.file

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import org.apache.commons.codec.digest.DigestUtils
import org.myddd.vertx.ioc.InstanceFactory
import java.io.InputStream

class FileDigestProvider:FileDigest {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    override suspend fun digest(path: String): Future<String> {
        return try {
            val exist = vertx.fileSystem().exists(path).await()
            require(exist)

            val digest = vertx.executeBlocking<String> {
                val md5 = DigestUtils.md5Hex(path)
                it.complete(md5)
            }.await()
            Future.succeededFuture(digest)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun digest(inputStream: InputStream): Future<String> {
        return try {
            val digest = vertx.executeBlocking<String> {
                val md5 = DigestUtils.md5Hex(inputStream)
                it.complete(md5)
            }.await()
            Future.succeededFuture(digest)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}