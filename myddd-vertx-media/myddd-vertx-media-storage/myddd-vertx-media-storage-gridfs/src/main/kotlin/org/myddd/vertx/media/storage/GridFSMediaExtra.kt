package org.myddd.vertx.media.storage

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaType

data class GridFSMediaExtra @JsonCreator constructor(@JsonProperty("fileId") val fileId:String) : MediaExtra(mediaType = MediaType.GridFs) {
    override fun destPath(): String {
        TODO("Not yet implemented")
    }
}
