package org.myddd.vertx.media.qcloud

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.region.Region
import io.netty.buffer.ByteBufInputStream
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaErrorCode
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaFile
import org.myddd.vertx.media.domain.MediaStorage
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime

class QCloudMediaStorage(private val secretId:String,private val secretKey:String,private val bucketName:String,private val region:String = "ap-guangzhou"): MediaStorage {



    companion object {
        private var storagePath: String = System.getProperty("java.io.tmpdir") + "STORAGE"
        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
    }

    private val cosClient by lazy {
        val cred = BasicCOSCredentials(secretId,secretKey)
        val region = Region(region)
        val clientConfig = ClientConfig(region)
        clientConfig.httpProtocol = HttpProtocol.https
        COSClient(cred,clientConfig)
    }

    override suspend fun uploadToStorage(mediaFile: MediaFile): Future<MediaExtra> {
        return try {
            val metadata = ObjectMetadata()
            metadata.contentLength = mediaFile.size
            val key = keyForFilePath(mediaFile.digest)

            val putObjectRequest = PutObjectRequest(bucketName, key, mediaFile.inputStream, metadata)
            val result = cosClient.putObject(putObjectRequest)
            requireNotNull(result){
                "上传文件至QCloud失败"
            }
            Future.succeededFuture(QCloudMediaExtra(key = key))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFromStorage(extra: MediaExtra): Future<InputStream> {
        return try {
            val fs = vertx.fileSystem()

            val mediaExtra = extra as QCloudMediaExtra

            val destPath = storagePath + File.separator + mediaExtra.key
            return if(fs.exists(destPath).await()){
                val buffer = fs.readFile(destPath).await()
                Future.succeededFuture(ByteBufInputStream(buffer.byteBuf))
            }else {
                val getObjectRequest = GetObjectRequest(bucketName, mediaExtra.key)
                val objectMetadata = cosClient.getObject(getObjectRequest)


                if(!fs.exists(storagePath).await()){
                    fs.mkdirs(storagePath)
                }

                val buffer = vertx.executeBlocking<Buffer> {
                    it.complete(Buffer.buffer(objectMetadata.objectContent.readAllBytes()))
                }.await()

                fs.writeFile(destPath,buffer).await()
                Future.succeededFuture(ByteBufInputStream(buffer.byteBuf))
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override fun loadMediaExtra(dbData: String): MediaExtra {
        val jsonObject = JsonObject(dbData)
        return jsonObject.mapTo(QCloudMediaExtra::class.java)
    }

    internal fun keyForFilePath(digest: String):String{
        val now = LocalDateTime.now()
        return "${now.year}/${now.monthValue}/${now.dayOfMonth}/$digest"
    }
}