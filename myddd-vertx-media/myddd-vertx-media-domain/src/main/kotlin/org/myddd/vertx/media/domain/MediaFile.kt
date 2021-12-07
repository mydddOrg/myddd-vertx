package org.myddd.vertx.media.domain

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.SourceFileNotExistsException
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

data class MediaFile(val inputStream:InputStream,val name:String,val size:Long,val digest:String) {

    companion object {

        suspend fun of(path:String):Future<MediaFile>{
            return try {
                val vertx = InstanceFactory.getInstance(Vertx::class.java)
                val fileDigest = InstanceFactory.getInstance(FileDigest::class.java)
                val fs = vertx.fileSystem()
                val exists = fs.exists(path).await()
                if(!exists){
                    throw SourceFileNotExistsException()
                }
                val props = fs.props(path).await()
                val digest = fileDigest.digest(FileInputStream(path)).await()
                Future.succeededFuture(
                    MediaFile(
                        inputStream = FileInputStream(path),
                        size = props.size(),
                        digest = digest,
                        name = File(path).name
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }

        }
    }

    suspend fun toBuffer():Future<Buffer>{
        val vertx = InstanceFactory.getInstance(Vertx::class.java)

        return try {
            val buffer = vertx.executeBlocking<Buffer> {
                it.complete(Buffer.buffer(inputStream.readAllBytes()))
            }.await()
            Future.succeededFuture(buffer)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}
