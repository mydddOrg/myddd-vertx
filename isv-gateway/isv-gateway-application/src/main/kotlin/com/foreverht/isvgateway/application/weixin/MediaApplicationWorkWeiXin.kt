package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.MediaApplication
import com.foreverht.isvgateway.api.dto.MediaDTO
import com.foreverht.isvgateway.application.AbstractApplication
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaErrorCode
import java.util.*

class MediaApplicationWorkWeiXin: AbstractApplication(),MediaApplication {

    override suspend fun uploadFile(isvAccessToken: String, path: String): Future<String> {
        return try {
            val (isvAuthCode,isvClientToken) = getAuthCode(isvAccessToken = isvAccessToken).await()

            val media = Media.createByLocalFile(path = path).await()
            Future.succeededFuture(media.mediaId)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun downloadFile(isvAccessToken: String, mediaId: String): Future<MediaDTO> {
        return try {
            val (isvAuthCode,isvClientToken) = getAuthCode(isvAccessToken = isvAccessToken).await()

            val media = Media.queryMediaById(mediaId = mediaId).await()
            if(Objects.nonNull(media)){
                throw BusinessLogicException(MediaErrorCode.MEDIA_NOT_FOUND)
            }


            Future.succeededFuture(MediaDTO(
                mediaId = mediaId,
                destPath = media!!.extra.destPath(),
                size = media.size,
                contentDisposition = "attachment; filename=\"${media.name}\""
            ))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}