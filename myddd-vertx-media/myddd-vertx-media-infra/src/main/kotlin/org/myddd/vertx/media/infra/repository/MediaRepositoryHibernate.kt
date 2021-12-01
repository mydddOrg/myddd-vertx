package org.myddd.vertx.media.infra.repository

import io.vertx.core.Future
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.Media
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class MediaRepositoryHibernate:EntityRepositoryHibernate(),MediaRepository {

    private val stringIDGenerator by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }

    override fun nextId(): String? {
        return stringIDGenerator.nextId()
    }

    override suspend fun queryByMediaId(mediaId: String): Future<Media?> {
        return singleQuery(
            clazz = Media::class.java,
            sql = "from Media where id = :mediaId",
            params = mapOf(
                "mediaId" to mediaId
            )
        )
    }

    override suspend fun queryByDigest(digest: String): Future<Media?> {
        return singleQuery(
            clazz = Media::class.java,
            sql = "from Media where digest = :digest",
            params = mapOf(
                "digest" to digest
            )
        )
    }

    override suspend fun saveMedia(media: Media): Future<Media> {
        return save(media)
    }
}