package org.myddd.vertx.media.qcloud

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaType

data class QCloudMediaExtra @JsonCreator constructor(
    @JsonProperty(value = "key")
    val key:String
    ):MediaExtra(mediaType = MediaType.QCloud) {

    companion object {
        private val STORAGE_DIR = System.getProperty("java.io.tmpdir") + "QCLOUD"
    }

    override fun destPath(): String {
        return "$STORAGE_DIR/$key"
    }

}