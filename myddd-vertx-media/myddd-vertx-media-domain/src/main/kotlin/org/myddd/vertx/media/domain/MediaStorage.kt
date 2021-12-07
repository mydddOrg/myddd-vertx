package org.myddd.vertx.media.domain

import io.vertx.core.Future

interface MediaStorage {

    suspend fun uploadToStorage(mediaFile: MediaFile):Future<MediaExtra>

    suspend fun downloadFromStorage(extra: MediaExtra):Future<String>

    fun loadMediaExtra(dbData: String): MediaExtra
}