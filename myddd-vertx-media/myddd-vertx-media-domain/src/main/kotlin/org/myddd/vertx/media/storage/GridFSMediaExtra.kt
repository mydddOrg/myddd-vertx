package org.myddd.vertx.media.storage

import org.myddd.vertx.media.domain.MediaExtra
import org.myddd.vertx.media.domain.MediaType
import java.beans.ConstructorProperties

data class GridFSMediaExtra  @ConstructorProperties(value = ["fileId"])constructor(val fileId: String) : MediaExtra(mediaType = MediaType.GridFs)