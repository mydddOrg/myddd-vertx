package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.bootstrap.validation.MediaValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.web.router.handler.AccessTokenAuthorizationHandler

class MediaRouter(vertx: Vertx, router: Router):AbstractISVRouter(vertx = vertx,router = router) {

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(MediaRouter::class.java) }
    }

    init {
        uploadMediaRoute()
        downloadMediaRoute()
    }

    private fun uploadMediaRoute(){
        createPostRoute(path = "/v1/medias"){ route ->

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.get<String>("clientId")
                        val accessToken = it.get<String>("accessToken")
                        val uploads: Set<FileUpload> = it.fileUploads()
                        val firstFile = uploads.first()
                        val mediaApplication = getMediaApplication(accessToken = accessToken).await()
                        val mediaId = mediaApplication.uploadFile(clientId = clientId,path = firstFile.uploadedFileName()).await()
                        it.end(JsonObject().put("mediaId",mediaId).toBuffer())
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
            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val mediaId = it.pathParam("mediaId")
                        val clientId = it.get<String>("clientId")
                        val accessToken = it.get<String>("accessToken")

                        val mediaApplication = getMediaApplication(accessToken = accessToken).await()
                        val mediaDTO = mediaApplication.downloadFile(clientId = clientId,mediaId = mediaId).await()

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