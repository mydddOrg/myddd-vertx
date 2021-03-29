package org.myddd.vertx.media.domain

import io.vertx.core.Future

interface MediaStorage {

    suspend fun uploadToStorage(tmpPath: String):Future<MediaExtra>

    suspend fun downloadFromStorage(extra: MediaExtra):Future<String>

    fun convertToEntityAttribute(dbData: String): MediaExtra
}