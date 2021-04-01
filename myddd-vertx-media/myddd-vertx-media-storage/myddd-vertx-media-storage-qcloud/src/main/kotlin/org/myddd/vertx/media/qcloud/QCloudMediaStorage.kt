package org.myddd.vertx.media.qcloud

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.region.Region
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
import java.io.File
import java.time.LocalDateTime

class QCloudMediaStorage(private val secretId:String,private val secretKey:String,private val bucketName:String,private val region:String = "ap-guangzhou"): MediaStorage {

    private val randomIdString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
    private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }
    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val cosClient by lazy {
        val cred = BasicCOSCredentials(secretId,secretKey)
        val region = Region(region)
        val clientConfig = ClientConfig(region)
        clientConfig.httpProtocol = HttpProtocol.https
        COSClient(cred,clientConfig)
    }

    override suspend fun uploadToStorage(tmpPath: String): Future<MediaExtra> {
        return try {
            val key = keyForFilePath(tmpPath).await()
            val putObjectRequest = PutObjectRequest(bucketName, key, File(tmpPath))
            val result = cosClient.putObject(putObjectRequest)
            requireNotNull(result){
                "上传文件至QCloud失败"
            }
            Future.succeededFuture(QCloudMediaExtra(key = key))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFromStorage(extra: MediaExtra): Future<String> {
        return try {
            val mediaExtra = extra as QCloudMediaExtra

            val downFile = File(mediaExtra.destPath())
            val getObjectRequest = GetObjectRequest(bucketName, mediaExtra.key)
            val downObjectMeta = cosClient.getObject(getObjectRequest, downFile)

            requireNotNull(downObjectMeta){
                "下载QCloud文件失败"
            }
            Future.succeededFuture(mediaExtra.destPath())
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override fun loadMediaExtra(dbData: String): MediaExtra {
        val jsonObject = JsonObject(dbData)
        return jsonObject.mapTo(QCloudMediaExtra::class.java)
    }

    internal suspend fun keyForFilePath(filePath: String):Future<String>{
        return try {
            val fs = vertx.fileSystem()
            val exists = fs.exists(filePath).await()
            if(!exists){
                throw BusinessLogicException(MediaErrorCode.SOURCE_FILE_NOT_EXISTS)
            }

            val now = LocalDateTime.now()
            val dir = "${now.year}/${now.monthValue}/${now.dayOfMonth}"

            val digest = fileDigest.digest(filePath).await()
            Future.succeededFuture("$dir/$digest")
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}