package org.myddd.vertx.media.storage

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaType

data class LocalMediaExtra @JsonCreator constructor(@JsonProperty("path") val path:String) :MediaExtra(mediaType = MediaType.LocalFile)