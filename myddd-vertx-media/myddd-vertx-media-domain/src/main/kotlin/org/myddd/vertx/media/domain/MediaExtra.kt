package org.myddd.vertx.media.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.myddd.vertx.media.storage.GridFSMediaExtra
import org.myddd.vertx.media.storage.LocalMediaExtra
import org.myddd.vertx.media.storage.QCloudMediaExtra

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "mediaType")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = LocalMediaExtra::class,name = "LocalFile"),
    JsonSubTypes.Type(value = GridFSMediaExtra::class,name = "GridFs"),
    JsonSubTypes.Type(value = QCloudMediaExtra::class,name = "QCloud")
])
abstract class MediaExtra(val mediaType:MediaType)