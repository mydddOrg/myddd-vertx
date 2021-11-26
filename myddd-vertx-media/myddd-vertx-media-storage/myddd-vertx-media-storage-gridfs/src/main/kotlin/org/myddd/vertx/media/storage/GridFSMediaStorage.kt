package org.myddd.vertx.media.storage

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.config.Config
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaStorage
import java.io.File

class GridFSMediaStorage : MediaStorage {

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private var storagePath: String = System.getProperty("java.io.tmpdir") + "STORAGE"

    private val config by lazy {
        val config = JsonObject()
        config.put("connection_string",Config.getString("mongodb.connection"))
        config
    }

    private val bucketName by lazy { Config.getString("mongodb.bucketName","fs") }

    private val mongoClient by lazy {
        MongoClient.create(vertx,config)
    }

    override suspend fun uploadToStorage(mediaFile: MediaFile): Future<MediaExtra> {
        return try {
            val fs = vertx.fileSystem()
            val gridFSClient = mongoClient.createGridFsBucketService(bucketName).await()

            val tmpFile = fs.createTempFile("gridFS",".tmp").await()
            val buffer = mediaFile.toBuffer().await()
            fs.writeFile(tmpFile,buffer)
            val uploadId = gridFSClient.uploadFile(tmpFile).await()

            Future.succeededFuture(GridFSMediaExtra(fileId = uploadId))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFromStorage(extra: MediaExtra): Future<String> {
        return try {
            val fs = vertx.fileSystem()
            val gridFSClient = mongoClient.createGridFsBucketService(bucketName).await()
            val gridFSMediaExtra = extra as GridFSMediaExtra

            val downloadFilePath = storagePath + File.separator + gridFSMediaExtra.fileId
            val exists = fs.exists(downloadFilePath).await()
            if(!exists){
                if(!fs.exists(storagePath + File.separator).await()){
                    fs.mkdirs(storagePath + File.separator)
                }
                gridFSClient.downloadFileByID(gridFSMediaExtra.fileId,downloadFilePath).await()
            }
            Future.succeededFuture(downloadFilePath)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override fun loadMediaExtra(dbData: String): MediaExtra {
        return JsonObject(dbData).mapTo(GridFSMediaExtra::class.java)
    }

}