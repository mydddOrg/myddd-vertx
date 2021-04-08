package org.myddd.vertx.media.storeage

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaErrorCode
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.string.RandomIDString
import java.time.LocalDateTime

class LocalMediaStorage(private var storagePath: String = System.getProperty("java.io.tmpdir") + "STORAGE") :MediaStorage {

    private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }
    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    override suspend fun uploadToStorage(tmpPath: String):Future<MediaExtra> {
        return try {
            val fs = vertx.fileSystem()

            val exists = fs.exists(tmpPath).await()
            if(!exists){
                throw BusinessLogicException(MediaErrorCode.SOURCE_FILE_NOT_EXISTS)
            }

            val destDir = randomFilePath().await()
            val digest = fileDigest.digest(tmpPath).await()
            val destPath ="$destDir/$digest"

            val destFileExists = fs.exists(destPath).await()
            if(!destFileExists){
                fs.copy(tmpPath,destPath).await()
            }

            Future.succeededFuture(LocalMediaExtra(path = destPath))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFromStorage(extra: MediaExtra): Future<String> {
        return try {
            val fs = vertx.fileSystem()
            val localMediaExtra = extra as LocalMediaExtra
            val exists = fs.exists(localMediaExtra.path).await()
            if(!exists){
                throw BusinessLogicException(MediaErrorCode.SOURCE_FILE_NOT_EXISTS)
            }
            Future.succeededFuture(extra.path)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override fun loadMediaExtra(dbData: String): MediaExtra {
        val jsonObject = JsonObject(dbData)
        return jsonObject.mapTo(LocalMediaExtra::class.java)
    }

    internal suspend fun randomFilePath():Future<String>{
        return try {
            val fs = vertx.fileSystem()
            val now = LocalDateTime.now()
            val dir = "$storagePath/${now.year}/${now.monthValue}/${now.dayOfMonth}"
            fs.mkdirs(dir).await()

            Future.succeededFuture(dir)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}