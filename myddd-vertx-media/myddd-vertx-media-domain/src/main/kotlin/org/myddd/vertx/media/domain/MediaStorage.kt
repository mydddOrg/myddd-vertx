package org.myddd.vertx.media.domain

import io.vertx.core.Future
import java.io.InputStream

interface MediaStorage {

    suspend fun uploadToStorage(mediaFile: MediaFile):Future<MediaExtra>

    suspend fun downloadFromStorage(extra: MediaExtra):Future<InputStream>

    fun loadMediaExtra(dbData: String): MediaExtra
}