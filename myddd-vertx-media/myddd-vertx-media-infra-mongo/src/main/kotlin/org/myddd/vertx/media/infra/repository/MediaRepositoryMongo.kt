package org.myddd.vertx.media.infra.repository

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.repository.mongo.DocumentEntityRepositoryMongo

class MediaRepositoryMongo:DocumentEntityRepositoryMongo(),MediaRepository {

    override fun nextId(): String? {
        return null
    }

    override suspend fun queryByMediaId(mediaId: String): Future<Media?> {
        return queryEntityById(Media::class.java, mediaId)
    }

    override suspend fun queryByDigest(digest: String): Future<Media?> {
        return singleQuery(Media::class.java, JsonObject().put("digest",digest))
    }

    override suspend fun saveMedia(media: Media): Future<Media> {
        return save(media)
    }
}