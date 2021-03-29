package org.myddd.vertx.media.domain.converter

import io.vertx.core.json.JsonObject
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaStorage
import javax.persistence.AttributeConverter

class MediaExtraConverter: AttributeConverter<MediaExtra, String> {

    private val mediaStorage by lazy { InstanceFactory.getInstance(MediaStorage::class.java) }

    override fun convertToDatabaseColumn(attribute: MediaExtra): String {
        return JsonObject.mapFrom(attribute).toString()
    }

    override fun convertToEntityAttribute(dbData: String): MediaExtra {
        return mediaStorage.loadMediaExtra(dbData)
    }

}