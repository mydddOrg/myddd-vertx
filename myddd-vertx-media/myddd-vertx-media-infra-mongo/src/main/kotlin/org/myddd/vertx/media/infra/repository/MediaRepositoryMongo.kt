package org.myddd.vertx.media.infra.repository

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo

class MediaRepositoryMongo:DocumentEntityRepositoryMongo(),MediaRepository {

    init {
        GlobalScope.launch {
            createDocument(Media::class.java).await()
        }
    }

    override fun nextId(): String? {
        return null
    }

    override suspend fun queryByMediaId(mediaId: String): Future<Media?> {
        return get(Media::class.java, mediaId)
    }

    override suspend fun queryByDigest(digest: String): Future<Media?> {
        return singleQuery(Media::class.java, JsonObject().put("digest",digest))
    }

    override suspend fun saveMedia(media: Media): Future<Media> {
        return save(media)
    }
}