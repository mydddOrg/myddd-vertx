package com.foreverht.isvgateway.application.workplus

import com.foreverht.isvgateway.api.MediaApplication
import com.foreverht.isvgateway.api.dto.MediaDTO
import com.foreverht.isvgateway.application.extention.accessToken
import com.foreverht.isvgateway.application.extention.api
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.file.AsyncFile
import io.vertx.core.file.OpenOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.ext.web.multipart.MultipartForm
import io.vertx.kotlin.coroutines.await
import org.apache.commons.codec.digest.DigestUtils
import org.myddd.vertx.ioc.InstanceFactory
import java.io.File
import java.util.*

class MediaApplicationWorkPlus:AbstractApplicationWorkPlus(),MediaApplication {

    private val webClient:WebClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }
    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
    override suspend fun uploadFile(isvAccessToken:String, path: String): Future<String> {
        return try {
            val fs = vertx.fileSystem()
            val name = path.substring(path.lastIndexOf(File.separator) + 1)
            val fileSystemProps = fs.lprops(path).await()

            val md5 = vertx.executeBlocking<String> {
                val md5 = DigestUtils.md5Hex(path)
                it.complete(md5)
            }.await()

            val mediaId = queryMediaIdByMd5(isvAccessToken = isvAccessToken,md5 =  md5).await()
            if(Objects.nonNull(mediaId)){
                logger.debug("命中MediaId缓存,${mediaId}")
                Future.succeededFuture(mediaId)
            }else{

                val form = MultipartForm.create()
                    .attribute("file_digest",md5)
                    .attribute("file_size", fileSystemProps.size().toString())
                    .attribute("filename",name)
                    .binaryFileUpload("file",name,path, "application/octet-stream ")

                val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
                val requestUrl = "${isvClientToken.api()}/medias?access_token=${isvClientToken.accessToken()}&file_digest=$md5&file_size=${fileSystemProps.size()}"
                logger.debug("【Request URL】:$requestUrl" )

                val response = webClient.postAbs(requestUrl)
                    .putHeader("content-type", "multipart/form-data")
                    .sendMultipartForm(form)
                    .await()

                if(response.resultSuccess()){
                    val bodyAsJson = response.bodyAsJsonObject()
                    Future.succeededFuture(bodyAsJson.getString("result"))
                }else{
                    Future.failedFuture(response.bodyAsString())
                }

            }




        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFile(isvAccessToken:String, mediaId: String): Future<MediaDTO> {

        var detFile:AsyncFile? = null
        return try {
            val destFilePath = System.getProperty("java.io.tmpdir") + mediaId
            detFile = vertx.fileSystem().open(destFilePath, OpenOptions()).await()


            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val requestUrl = "${isvClientToken.api()}/medias/$mediaId?access_token=${isvClientToken.accessToken()}&type=id"
            val response = webClient.getAbs(requestUrl)
                .`as`(BodyCodec.pipe(detFile))
                .send()
                .await()
            logger.debug(response.headers())
            val contentType = response.getHeader("Content-Type")
            if(response.statusCode() == 200 && contentType != "application/json"){
                Future.succeededFuture(
                    MediaDTO(
                        mediaId = mediaId,
                        contentType = response.getHeader("Content-Type"),
                        size =  response.getHeader("Content-Length").toLong(),
                        contentDisposition = response.getHeader("Content-Disposition"),
                        destPath = destFilePath
                    ))
            }else{
                Future.failedFuture(response.bodyAsString())
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun queryMediaIdByMd5(isvAccessToken: String,md5: String):Future<String?>{
        return try {
            val isvClientToken = getRemoteAccessToken(isvAccessToken).await()
            val requestUrl = "${isvClientToken.api()}/medias/$md5/info/?access_token=${isvClientToken.accessToken()}&type=DIGEST"

            val response = webClient.getAbs(requestUrl)
                .send()
                .await()

            if(response.resultSuccess()){
                val bodyJson = response.bodyAsJsonObject()
                val mediaId = bodyJson.getJsonObject("result").getString("id")
                Future.succeededFuture(mediaId)
            }else{
                Future.succeededFuture()
            }

        }catch(t:Throwable){
            Future.succeededFuture()
        }
    }
}