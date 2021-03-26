package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.bootstrap.ext.jsonFormatEnd
import com.foreverht.isvgateway.bootstrap.handler.ISVAccessTokenAuthorizationHandler
import com.foreverht.isvgateway.bootstrap.validation.MediaValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MediaRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(MediaRoute::class.java) }
    }

    init {
        uploadMediaRoute()
        downloadMediaRoute()
    }

    private fun uploadMediaRoute(){
        createPostRoute(path = "/v1/medias"){ route ->

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val uploads: Set<FileUpload> = it.fileUploads()
                        val firstFile = uploads.first()
                        val mediaApplication = getMediaApplication(accessToken = accessToken).await()
                        val mediaId = mediaApplication.uploadFile(isvAccessToken = accessToken,path = firstFile.uploadedFileName()).await()
                        it.jsonFormatEnd(JsonObject().put("mediaId",mediaId).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }

            }

        }.consumes("multipart/form-data")
    }




    private fun downloadMediaRoute(){
        createGetRoute(path = "/$version/medias/:mediaId"){ route ->

            route.handler(MediaValidationHandler().downloadMediaValidationHandler())
            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val mediaId = it.pathParam("mediaId")
                        val accessToken = it.get<String>("accessToken")

                        val mediaApplication = getMediaApplication(accessToken = accessToken).await()
                        val mediaDTO = mediaApplication.downloadFile(isvAccessToken = accessToken,mediaId = mediaId).await()

                        it.response().putHeader("Content-Type",mediaDTO.contentType)
                        it.response().putHeader("Content-Length",mediaDTO.size.toString())
                        it.response().putHeader("Content-Disposition",mediaDTO.contentDisposition)

                        it.response().sendFile(mediaDTO.destPath)

                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }


        }
    }
}