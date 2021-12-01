package org.myddd.vertx.media.domain

import io.vertx.core.Future


interface MediaRepository {
    fun nextId():String?

    suspend fun queryByMediaId(mediaId:String):Future<Media?>

    suspend fun queryByDigest(digest:String):Future<Media?>

    suspend fun saveMedia(media: Media):Future<Media>
}