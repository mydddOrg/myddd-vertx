package org.myddd.vertx.media.domain

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

data class MediaFile(val inputStream:InputStream,val name:String,val size:Long,val digest:String) {
    companion object {

        private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }
        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

        suspend fun of(path:String):Future<MediaFile>{
            return try {
                val fs = vertx.fileSystem()
                val exists = fs.exists(path).await()
                if(!exists){
                    throw BusinessLogicException(MediaErrorCode.SOURCE_FILE_NOT_EXISTS)
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
}
