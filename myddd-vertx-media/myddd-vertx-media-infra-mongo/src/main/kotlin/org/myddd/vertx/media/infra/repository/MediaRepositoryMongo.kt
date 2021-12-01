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
        return singleQuery(JsonObject().put("_id",mediaId),Media::class.java)
    }

    override suspend fun queryByDigest(digest: String): Future<Media?> {
        return singleQuery(JsonObject().put("digest",digest),Media::class.java)
    }

    override suspend fun saveMedia(media: Media): Future<Media> {
        return insert(media)
    }
}